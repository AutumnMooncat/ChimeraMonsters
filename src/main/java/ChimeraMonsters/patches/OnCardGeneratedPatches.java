package ChimeraMonsters.patches;

import ChimeraMonsters.powers.AbstractModifierPower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

import java.util.ArrayList;

public class OnCardGeneratedPatches {

    public static AbstractCard onGenerateInCombatCard(AbstractCard card) {
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            for (AbstractPower power : monster.powers) {
                if (power instanceof AbstractModifierPower) {
                    card = ((AbstractModifierPower) power).modifyGeneratedCard(card);
                }
            }
        }
        return card;
    }

    @SpirePatch2(clz = CardRewardScreen.class, method = "customCombatOpen")
    public static class DiscoveryStyleCards {
        @SpirePrefixPatch
        public static void affect(ArrayList<AbstractCard> choices) {
            ArrayList<AbstractCard> cards = new ArrayList<>();
            for (AbstractCard c : choices) {
                cards.add(onGenerateInCombatCard(c));
            }
            choices.clear();
            choices.addAll(cards);
        }
    }

    @SpirePatch2(clz = ShowCardAndAddToHandEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class})
    @SpirePatch2(clz = ShowCardAndAddToHandEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, float.class, float.class})
    @SpirePatch2(clz = ShowCardAndAddToDrawPileEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, boolean.class, boolean.class})
    @SpirePatch2(clz = ShowCardAndAddToDrawPileEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, float.class, float.class, boolean.class, boolean.class, boolean.class})
    @SpirePatch2(clz = ShowCardAndAddToDiscardEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class})
    @SpirePatch2(clz = ShowCardAndAddToDiscardEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, float.class, float.class})
    public static class CreatedCards {
        @SpirePrefixPatch
        public static void affect(Object[] __args) {
            if (__args[0] instanceof AbstractCard) {
                __args[0] = onGenerateInCombatCard((AbstractCard) __args[0]);
            }
        }
    }
}
