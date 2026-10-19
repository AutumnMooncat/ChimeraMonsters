package ChimeraMonsters.modifiers.monsters.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.AggressivePower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class AggressiveMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(AggressiveMod.class.getSimpleName());
    public static final int AMOUNT = 1;

    public AggressiveMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return hasBlockTurn(monster) && noHalfDead(monster);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new AggressivePower(monster));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new AggressiveMod();
    }
}
