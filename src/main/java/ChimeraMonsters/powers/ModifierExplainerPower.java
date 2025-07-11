package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class ModifierExplainerPower extends AbstractEasyPower implements InvisiblePower, NonStackablePower {
    public ModifierExplainerPower(AbstractCreature owner, String name, String description) {
        super(ChimeraMonstersMod.makeID(ModifierExplainerPower.class.getSimpleName()), name, NeutralPowertypePatch.NEUTRAL, false, owner, 0);
        this.description = description;
    }
}
