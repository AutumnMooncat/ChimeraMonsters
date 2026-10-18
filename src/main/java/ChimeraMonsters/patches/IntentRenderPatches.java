package ChimeraMonsters.patches;

import ChimeraMonsters.powers.interfaces.IntentHidingPower;
import ChimeraMonsters.util.IntentManager;
import ChimeraMonsters.util.IntentPayload;
import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;

public class IntentRenderPatches {
    public static boolean hideIntent(AbstractMonster monster) {
        boolean ret = false;
        for (AbstractPower power : monster.powers) {
            if (power instanceof IntentHidingPower) {
                ret |= ((IntentHidingPower) power).shouldHide();
            }
        }
        return ret;
    }

    @SpirePatch(clz = AbstractMonster.class, method = "renderTip")
    public static class TipPatch {
        @SpireInsertPatch (locator = Locator.class)
        public static void Insert(AbstractMonster __instance, SpriteBatch sb, ArrayList<PowerTip> ___tips, PowerTip ___intentTip) {
            if (hideIntent(__instance)) {
                ___tips.remove(___intentTip);
                return;
            }
            int index = ___tips.indexOf(___intentTip);
            for (IntentPayload intent : IntentManager.extraIntents(__instance)) {
                index++;
                ___tips.add(index, intent.fields.intentTip);
            }
        }
        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(ArrayList.class, "isEmpty"));
            }
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "render")
    @SpirePatch(clz = CustomMonster.class, method = "render")
    public static class IntentPatch {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("hasRelic")) {
                        m.replace("{$_ = $proceed($$) || " + IntentRenderPatches.class.getName() + ".hideIntent(this);}");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "renderIntent")
    public static class IntentStuff {
        public static boolean lock;

        @SpirePostfixPatch
        public static void doExtras(AbstractMonster __instance, SpriteBatch sb) {
            if (lock || !IntentManager.hasExtraIntents(__instance)) return;
            lock = true;
            IntentManager.backupMainIntent(__instance);
            for (IntentPayload pl : IntentManager.extraIntents(__instance)) {
                IntentManager.setActivePayload(__instance, pl);
                pl.render(sb);
                IntentManager.releaseActivePayload(__instance, pl);
            }
            IntentManager.restoreMainIntent(__instance);
            lock = false;
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "renderDamageRange")
    public static class IntentDamageStuff {
        public static boolean lock;

        @SpirePostfixPatch
        public static void doExtras(AbstractMonster __instance, SpriteBatch sb) {
            if (lock || !IntentManager.hasExtraIntents(__instance)) return;
            lock = true;
            IntentManager.backupMainIntent(__instance);
            for (IntentPayload pl : IntentManager.extraIntents(__instance)) {
                IntentManager.setActivePayload(__instance, pl);
                pl.renderDamage(sb);
                IntentManager.releaseActivePayload(__instance, pl);
            }
            IntentManager.restoreMainIntent(__instance);
            lock = false;
        }
    }
}
