package ChimeraMonsters.patches;

import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.util.FightModificationManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.unique.SummonGremlinAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;

public class SpawnMonsterPatch {
    @SpirePatch2(clz = SummonGremlinAction.class, method = "update")
    @SpirePatch2(clz = SpawnMonsterAction.class, method = "update")
    public static class RollOnSpawn {
        @SpireInsertPatch(locator = Locator.class)
        public static void plz(AbstractMonster ___m) {
            FightModificationManager.rollMonsterModifier(___m, AbstractDungeon.getMonsters());
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher match = new Matcher.MethodCallMatcher(AbstractMonster.class, "init");
                return LineFinder.findInOrder(ctBehavior, match);
            }
        }
    }

    @SpirePatch2(clz = SpawnMonsterAction.class, method = "update")
    public static class AlsoDoPreBattle {
        private static boolean firstPass;

        @SpirePrefixPatch
        public static void plz(boolean ___used) {
            firstPass = !___used;
        }

        @SpirePostfixPatch
        public static void plz2(AbstractMonster ___m) {
            if (firstPass) {
                for (AbstractMonsterModifier mod : MonsterFields.receivedModifiers.get(___m)) {
                    mod.preBattleActions(___m, AbstractDungeon.getMonsters());
                }
            }
        }
    }

    @SpirePatch2(clz = SummonGremlinAction.class, method = "update")
    public static class AlsoDoPreBattleForThisOne {
        @SpirePostfixPatch
        public static void plz(SummonGremlinAction __instance, AbstractMonster ___m) {
            if (__instance.isDone) {
                for (AbstractMonsterModifier mod : MonsterFields.receivedModifiers.get(___m)) {
                    mod.preBattleActions(___m, AbstractDungeon.getMonsters());
                }
            }
        }
    }
}
