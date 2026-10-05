package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.GiantPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class GiantMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(GiantMod.class.getSimpleName());

    public GiantMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noBossCheck(monster, context);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new GiantPower(monster));
        manipulateBaseDamage(monster, BUFF_50);
        manipulateBaseHealth(monster, BUFF_50);
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new GiantMod();
    }
}
