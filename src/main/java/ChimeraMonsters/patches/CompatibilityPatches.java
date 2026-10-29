package ChimeraMonsters.patches;

import ChimeraMonsters.util.IntentManager;
import ChimeraMonsters.util.IntentPayload;
import ChimeraMonsters.util.MonsterOrbHelper;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;

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
}
