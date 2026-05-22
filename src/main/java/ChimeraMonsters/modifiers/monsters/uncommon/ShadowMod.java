package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.ShadowPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class ShadowMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(ShadowMod.class.getSimpleName());

    public ShadowMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return true;
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseDamage(monster, BUFF_50);
        manipulateBaseHealth(monster, DEBUFF_33);
        applyPowersToCreature(monster, new ShadowPower(monster, 1));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new ShadowMod();
    }
}
