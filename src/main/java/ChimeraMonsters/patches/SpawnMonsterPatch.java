package ChimeraMonsters.patches;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.util.FightModificationManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;

public class SpawnMonsterPatch {
    @SpirePatch2(clz = SpawnMonsterAction.class, method = "update")
    public static class RollOnSpawn {
        @SpireInsertPatch(locator =  Locator.class)
        public static void plz(AbstractMonster ___m) {
            FightModificationManager.rollMonsterModifier(___m, AbstractDungeon.getMonsters());
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher match = new Matcher.MethodCallMatcher(AbstractMonster.class, "showHealthBar");
                return LineFinder.findInOrder(ctBehavior, match);
            }
        }
    }
}
