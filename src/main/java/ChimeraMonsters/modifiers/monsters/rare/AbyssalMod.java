package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.AbyssalPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class AbyssalMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(AbyssalMod.class.getSimpleName());

    public AbyssalMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noEliteOrBossCheck(monster, context) && actAtLeast(2);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new AbyssalPower(monster, scaleAbilities(monster, 1, 2)));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new AbyssalMod();
    }
}
