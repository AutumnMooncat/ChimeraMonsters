package ChimeraMonsters.powers;

import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.core.AbstractCreature;

public abstract class AbstractInternalLogicPower extends AbstractEasyPower implements InvisiblePower {
    public AbstractInternalLogicPower(String ID, AbstractCreature owner, int amount) {
        super(ID, ID, NeutralPowertypePatch.NEUTRAL, false, owner, amount);
    }
}
