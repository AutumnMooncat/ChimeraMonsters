package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.PainfulStabsPower;

public class StabbingMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(StabbingMod.class.getSimpleName());

    public StabbingMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noBossCheck(monster, context) && actAtLeast(2) && !hasAnyAnywhere(monster, PainfulStabsPower.class);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseDamage(monster, DEBUFF_10);
        applyPowersToCreature(monster, new PainfulStabsPower(monster));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new StabbingMod();
    }
}
