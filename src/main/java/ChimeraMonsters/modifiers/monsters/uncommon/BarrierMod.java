package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.BarrierPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.ThornsPower;

public class BarrierMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(BarrierMod.class.getSimpleName());

    public BarrierMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        if (hasAnyAnywhere(monster, PlatedArmorPower.class, BarricadePower.class, MetallicizePower.class, ThornsPower.class)) {
            return false;
        }
        return hasBlockTurn(monster);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new BarrierPower(monster, 1));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new BarrierMod();
    }
}
