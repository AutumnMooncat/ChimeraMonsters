package ChimeraMonsters.patches;

import ChimeraMonsters.powers.interfaces.OnOtherMonsterKilledPower;
import ChimeraMonsters.util.MonsterOrbHelper;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;

import java.util.ArrayList;

public class OnMonsterDeathPatch {
    @SpirePatch2(clz = AbstractMonster.class, method = "die", paramtypez = {boolean.class})
    public static class HeDed {
        @SpireInsertPatch(locator = Locator.class)
        public static void plz(AbstractMonster __instance) {
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                if (!monster.isDeadOrEscaped()) {
                    for (AbstractPower power : monster.powers) {
                        if (power instanceof OnOtherMonsterKilledPower) {
                            ((OnOtherMonsterKilledPower) power).onMonsterKilled(__instance);
                        }
                    }
                }
            }
            ArrayList<AbstractOrb> orbs = MonsterOrbHelper.getOrbs(__instance);
            if (orbs != null)
            {
                orbs.clear();
            }
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(MonsterGroup.class, "areMonstersBasicallyDead"));
            }
        }
    }
}
