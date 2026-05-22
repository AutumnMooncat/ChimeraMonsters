package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.InterceptionPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class InterceptingMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(InterceptingMod.class.getSimpleName());

    public InterceptingMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return checkContext(context, multiCombat) && checkContext(context, this, onePerFight);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new InterceptionPower(monster, 10));
    }


    @Override
    public AbstractMonsterModifier makeCopy() {
        return new InterceptingMod();
    }
}
