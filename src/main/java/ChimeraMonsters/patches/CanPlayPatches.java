package ChimeraMonsters.patches;

import ChimeraMonsters.powers.AbstractModifierPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class CanPlayPatches {
    @SpirePatch(clz = AbstractCard.class, method = "hasEnoughEnergy")
    public static class CardModifierCanPlayCard {
        public static SpireReturn<Boolean> Prefix(AbstractCard __instance) {
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                for (AbstractPower power : monster.powers) {
                    if (power instanceof AbstractModifierPower) {
                        if (((AbstractModifierPower) power).preventPlaying(__instance)) {
                            return SpireReturn.Return(false);
                        }
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }
}
