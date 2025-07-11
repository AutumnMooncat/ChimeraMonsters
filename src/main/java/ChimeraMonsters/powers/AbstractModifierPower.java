package ChimeraMonsters.powers;


import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

public abstract class AbstractModifierPower extends AbstractEasyPower {
    public AbstractModifierPower(String ID, String name, PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount) {
        super(ID, name, powerType, isTurnBased, owner, amount);
    }

    public AbstractCard modifyGeneratedCard(AbstractCard card) {
        return card;
    }

    public float modifyMonsterBlock(float blockAmount) {
        return blockAmount;
    }

    public boolean preventPlaying(AbstractCard card) {
        return false;
    }
}
