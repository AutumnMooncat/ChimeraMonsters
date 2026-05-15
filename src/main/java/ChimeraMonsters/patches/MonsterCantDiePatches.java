package ChimeraMonsters.patches;

import ChimeraMonsters.powers.interfaces.MonsterCantDiePower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class MonsterCantDiePatches {
    @SpirePatch2(clz = AbstractMonster.class, method = "damage")
    public static class DontBonkDamage {
        // Also called by dynamic patch if class doesnt call super
        @SpirePostfixPatch
        public static void plz(AbstractMonster __instance) {
            // This may end up being needed if the current workaround proves janky, set up just in case
        }
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "die", paramtypez = {boolean.class})
    public static class DontBonkDie {
        // Also called by dynamic patch if class doesnt call super
        @SpirePrefixPatch
        public static SpireReturn<?> plz(AbstractMonster __instance) {
            MonsterCantDiePower pow = null;
            for (AbstractPower power : __instance.powers) {
                if (power instanceof MonsterCantDiePower) {
                    if (((MonsterCantDiePower) power).cantDie(__instance)) {
                        pow = (MonsterCantDiePower) power;
                        break;
                    }
                }
            }
            if (pow != null) {
                if (!__instance.halfDead) {
                    pow.onPreventDeath(__instance);
                    __instance.halfDead = true;
                }
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = MonsterGroup.class, method = "areMonstersDead")
    @SpirePatch2(clz = MonsterGroup.class, method = "areMonstersBasicallyDead")
    public static class ItsNotOverYet {
        @SpirePostfixPatch
        public static boolean plz(boolean __result, MonsterGroup __instance) {
            for (AbstractMonster monster : __instance.monsters) {
                for (AbstractPower power : monster.powers) {
                    if (power instanceof MonsterCantDiePower) {
                        if (((MonsterCantDiePower) power).cantDie(monster)) {
                            return false;
                        }
                    }
                }
            }
            return __result;
        }
    }
}
