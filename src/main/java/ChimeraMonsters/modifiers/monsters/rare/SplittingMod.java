package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.SplittingPower;
import ChimeraMonsters.util.matchers.SuperFieldAccessMatcher;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.actions.unique.CannotLoseAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.MinionPower;

public class SplittingMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(SplittingMod.class.getSimpleName());

    public SplittingMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        if (monster.type != AbstractMonster.EnemyType.NORMAL || monster.hasPower(MinionPower.POWER_ID)) {
            return false;
        }
        return noHalfDead(monster) && !hasAnyAnywhere(monster, CannotLoseAction.class, CanLoseAction.class, SuicideAction.class, HealAction.class);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new SplittingPower(monster, -1)); // TODO - Failing and ending combat on split?
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new SplittingMod();
    }
}
