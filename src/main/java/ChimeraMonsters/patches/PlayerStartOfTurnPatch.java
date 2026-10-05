package ChimeraMonsters.patches;

import ChimeraMonsters.powers.interfaces.MonsterAtPlayerStartOfTurnPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class PlayerStartOfTurnPatch {
    @SpirePatch2(clz = AbstractCreature.class, method = "applyStartOfTurnPowers")
    public static class FireMonsterHook {
        @SpirePostfixPatch
        public static void plz(AbstractCreature __instance) {
            if (__instance.isPlayer) {
                for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                    if (!monster.isDeadOrEscaped()) {
                        for (AbstractPower power : monster.powers) {
                            if (power instanceof MonsterAtPlayerStartOfTurnPower) {
                                ((MonsterAtPlayerStartOfTurnPower) power).atPlayerStartOfTurn();
                            }
                        }
                    }
                }
            }
        }
    }
}