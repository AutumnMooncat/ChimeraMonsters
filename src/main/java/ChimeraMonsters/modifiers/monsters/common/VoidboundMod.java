package ChimeraMonsters.modifiers.monsters.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.VoidboundPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class VoidboundMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(VoidboundMod.class.getSimpleName());

    public VoidboundMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return checkContext(context, multiCombat);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new VoidboundPower(monster, 1));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new VoidboundMod();
    }
}
