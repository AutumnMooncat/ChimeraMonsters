package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.TinyPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class TinyMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(TinyMod.class.getSimpleName());

    public TinyMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noBossCheck(monster, context);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new TinyPower(monster));
        manipulateBaseDamage(monster, DEBUFF_33);
        manipulateBaseHealth(monster, DEBUFF_33);
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new TinyMod();
    }
}
