package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class StickyPower extends AbstractInternalLogicPower implements IntentInterceptingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(StickyPower.class.getSimpleName());

    public StickyPower(AbstractCreature owner, int amount) {
        super(POWER_ID, owner, amount);
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        if (intendedMove.intent == AbstractMonster.Intent.ATTACK || intendedMove.intent == AbstractMonster.Intent.ATTACK_BUFF || intendedMove.intent == AbstractMonster.Intent.ATTACK_DEBUFF || intendedMove.intent == AbstractMonster.Intent.ATTACK_DEFEND) {
            return 1f;
        }
        return 0f;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        overrideMove(owner, new EnemyMoveInfo(replacedMove.nextMove, AbstractMonster.Intent.ATTACK_DEBUFF, replacedMove.baseDamage, replacedMove.multiplier, replacedMove.isMultiDamage));
    }

    @Override
    public boolean performIntercept() {
        if (owner instanceof AbstractMonster) {
            AbstractMonster ownerMon = (AbstractMonster) owner;
            ownerMon.takeTurn();
            addToBot(new MakeTempCardInDiscardAction(new Slimed(), amount));
        }
        return true;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }
}
