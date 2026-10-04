package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.CloakedPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class CloakedMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(CloakedMod.class.getSimpleName());

    public CloakedMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noBossCheck(monster, context);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new CloakedPower(monster));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new CloakedMod();
    }
}
