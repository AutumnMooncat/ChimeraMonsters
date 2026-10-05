package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.TemporalPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.RegenPower;
import com.megacrit.cardcrawl.powers.RegenerateMonsterPower;
import com.megacrit.cardcrawl.powers.SharpHidePower;
import com.megacrit.cardcrawl.powers.ThornsPower;

public class TemporalMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(TemporalMod.class.getSimpleName());

    public TemporalMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return !hasAnyAnywhere(monster, RegenerateMonsterPower.class, RegenPower.class, ThornsPower.class, SharpHidePower.class);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new TemporalPower(monster));
        manipulateBaseHealth(monster, DEBUFF_20);
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new TemporalMod();
    }
}
