package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.SplittingPower;
import ChimeraMonsters.util.matchers.SuperFieldAccessMatcher;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class SplittingMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(SplittingMod.class.getSimpleName());

    public SplittingMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return monster.type == AbstractMonster.EnemyType.NORMAL &&
                !hasAnyAnywhere(monster, new SuperFieldAccessMatcher(AbstractCreature.class, "halfDead"));
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new SplittingPower(monster, -1));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new SplittingMod();
    }
}
