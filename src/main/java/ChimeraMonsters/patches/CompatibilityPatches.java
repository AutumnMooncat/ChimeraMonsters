package ChimeraMonsters.patches;

import ChimeraMonsters.ChimeraMonstersConfig;
import ChimeraMonsters.powers.interfaces.SplitPointPower;
import ChimeraMonsters.util.IntentManager;
import ChimeraMonsters.util.IntentPayload;
import ChimeraMonsters.util.MonsterOrbHelper;
import ChimeraMonsters.util.compat.MintyCompat;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import java.util.Set;
import java.util.TreeSet;

public class CompatibilityPatches {
    @SpirePatch2(cls = "mintySpire.patches.ui.RenderIncomingDamagePatches$TIDHook", method = "hook", requiredModId = "mintyspire")
    public static class MintyDamageSum {
        @SpireInsertPatch(locator = Locator.class, localvars = {"m", "dmg"})
        public static void plz(Object[] __args, AbstractMonster m, @ByRef int[] dmg) {
            for (IntentPayload pl : IntentManager.extraIntents(m)) {
                int tmp = pl.fields.intentDmg;
                if (pl.fields.isMultiDmg) {
                    tmp *= pl.fields.intentMultiAmt;
                }

                if (tmp > 0) {
                    dmg[0] += tmp;
                }
            }

            dmg[0] += MonsterOrbHelper.intendedOrbDamage(m);
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractMonster.class, "getIntentDmg"));
            }
        }
    }

    @SpirePatch2(cls = "mintySpire.patches.ui.RenderIncomingDamagePatches", method = "isAttacking", requiredModId = "mintyspire")
    public static class MintyDamageIsAttacking {
        @SpirePostfixPatch
        public static boolean plz(boolean __result, AbstractMonster m) {
            for (IntentPayload pl : IntentManager.extraIntents(m)) {
                if (pl.fields.intentBaseDmg >= 0) {
                    return true;
                }
            }
            if (MonsterOrbHelper.intendedOrbDamage(m) > 0) {
                return true;
            }
            return __result;
        }
    }

    @SpirePatch2(cls = "mintySpire.patches.monsters.HalfwayHealthbarTextPatch$TextRender", method = "getHPTextAddition", requiredModId = "mintyspire")
    public static class MintySplitPointText {
        @SpirePostfixPatch
        public static String plz(String __result, AbstractCreature c) {
            if (c.powers.stream().anyMatch(p -> p instanceof SplitPointPower)) {
                return "";
            }
            return __result;
        }
    }

    @SpirePatch2(clz = AbstractCreature.class, method = "renderHealthText", requiredModId = "mintyspire")
    public static class MintyRenderSplitPoint {
        @SpireInstrumentPatch
        public static ExprEditor plz() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(FontHelper.class.getName()) && m.getMethodName().equals("renderFontCentered")) {
                        m.replace(String.format("$3 = %s.modifyString(this, $3); $proceed($$);", MintyRenderSplitPoint.class.getName()));
                    }
                }
            };
        }

        public static String modifyString(AbstractCreature c, String orig) {
            if (c.isDead || c.isDying) {
                return orig;
            }
            if (Loader.isModLoaded("mintyspire") && MintyCompat.mintyShowsHalfHealth(c) && c.powers.stream().anyMatch(p -> p instanceof SplitPointPower)) {
                Set<Integer> toRender = new TreeSet<>(Integer::compareTo);
                if (MintyCompat.mintyAlreadyRenders(c)) {
                    toRender.add(c.maxHealth/2);
                }
                for (AbstractPower power : c.powers) {
                    if (power instanceof SplitPointPower) {
                        toRender.add(((SplitPointPower) power).splitPoint());
                    }
                }
                StringBuilder origBuilder = new StringBuilder(orig);
                for (Integer i : toRender) {
                    origBuilder.append(" (").append(i).append(")");
                }
                orig = origBuilder.toString();
            }
            return orig;
        }
    }

    @SpirePatch2(cls = "spirepeople.patches.AlwaysDisplayNamesPatch", method = "renderColoredOrAnimatedName", requiredModId = "SpirePeople")
    public static class SpirePeopleFancyNames {
        @SpireInstrumentPatch
        public static ExprEditor plz() {
            return new ExprEditor() {
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.isReader() && f.getFieldName().equals("alwaysDisplayNames")) {
                        f.replace(String.format("$_ = $proceed($$) || %s.alwaysShowName();", ChimeraMonstersConfig.class.getName()));
                    }
                }
            };
        }

        @SpireInsertPatch(locator = Locator.class, localvars = {"y"})
        public static void plz2(Object[] __args, @ByRef float[] y) {
            if (ChimeraMonstersConfig.alwaysShowName()) {
                y[0] += CreatureRenderPatches.NameStuff.nameOffset();
            }
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(SpireField.class, "get"));
            }
        }
    }
}
