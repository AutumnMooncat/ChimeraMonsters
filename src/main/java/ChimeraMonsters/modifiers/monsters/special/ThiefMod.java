package ChimeraMonsters.modifiers.monsters.special;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.ThiefPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class ThiefMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(ThiefMod.class.getSimpleName());

    public ThiefMod() {
        super(ID, ModifierRarity.SPECIAL);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return true;
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseDamage(monster, DEBUFF_25);
        manipulateBaseHealth(monster, DEBUFF_25);
        applyPowersToCreature(monster, new ThiefPower(monster, 1));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new ThiefMod();
    }
}
