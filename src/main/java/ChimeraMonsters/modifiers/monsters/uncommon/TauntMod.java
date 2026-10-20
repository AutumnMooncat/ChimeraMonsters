package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.TauntPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class TauntMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(TauntMod.class.getSimpleName());

    public TauntMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return checkContext(context, multiCombat.and(noScaling).and(noSummoning));
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new TauntPower(monster, 1));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new TauntMod();
    }
}
