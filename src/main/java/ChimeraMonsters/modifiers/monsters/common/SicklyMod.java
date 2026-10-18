package ChimeraMonsters.modifiers.monsters.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.SickPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class SicklyMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(SicklyMod.class.getSimpleName());

    public SicklyMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return checkContext(context, multiCombat);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new SickPower(monster));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new SicklyMod();
    }
}
