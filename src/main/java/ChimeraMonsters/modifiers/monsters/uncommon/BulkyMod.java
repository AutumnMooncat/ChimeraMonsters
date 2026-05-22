package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class BulkyMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(BulkyMod.class.getSimpleName());

    public BulkyMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return hasBlockTurn(monster);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateFinalBlock(monster, BUFF_33);
        manipulateBaseDamage(monster, DEBUFF_20);
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new BulkyMod();
    }
}
