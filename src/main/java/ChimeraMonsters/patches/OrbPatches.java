package ChimeraMonsters.patches;

import ChimeraMonsters.util.MonsterOrbHelper;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import javassist.CtBehavior;

public class OrbPatches {
    @SpirePatch2(clz = MonsterGroup.class, method = "applyPreTurnLogic")
    public static class PreTurn {
        @SpireInsertPatch(locator = Locator.class, localvars = {"m"})
        public static void plz(AbstractMonster m) {
            MonsterOrbHelper.applyStartOfTurnOrbs(m);
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractMonster.class, "applyStartOfTurnPowers"));
            }
        }
    }



    @SpirePatch2(clz = GameActionManager.class, method = "getNextAction")
    public static class PostTurn {
        @SpireInsertPatch(locator = Locator.class, localvars = {"m"})
        public static void plz(AbstractMonster m) {
            MonsterOrbHelper.applyEndOfTurnOrbs(m);
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractMonster.class, "applyTurnPowers"));
            }
        }
    }
}
