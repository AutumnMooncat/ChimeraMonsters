package ChimeraMonsters.modifiers.monsters.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.VampiricPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class VampiricMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(VampiricMod.class.getSimpleName());
    public static final int PERCENTAGE = 50;

    public VampiricMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return true; //TODO: Scaling,Multi Enemy Fight or On-hit check
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new VampiricPower(monster, PERCENTAGE));
        startDamaged(monster, 0.7f);
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new VampiricMod();
    }
}
