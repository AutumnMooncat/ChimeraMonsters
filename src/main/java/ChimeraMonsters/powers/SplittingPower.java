package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.actions.SplitMonsterAction;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.powers.interfaces.SplitPointPower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class SplittingPower extends AbstractEasyPower implements IntentInterceptingPower, SplitPointPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(SplittingPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean activated;

    public SplittingPower(AbstractCreature owner) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, -1);
        priority = -5;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        boolean wasActivated = activated;
        activated = (owner.currentHealth - damageAmount) <= owner.maxHealth/2;
        if (!wasActivated && activated && owner instanceof AbstractMonster) {
            flash();
            addToBot(new DoAction(() -> {
                removeAndResetInterceptor(owner);
                applyInterceptor(owner, this, getMove(owner));
            }));
        }
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        if (activated) {
            return 1f;
        }
        return 0f;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        EnemyMoveInfo newMove = new EnemyMoveInfo(replacedMove.nextMove, AbstractMonster.Intent.UNKNOWN, -1 , 0, false);
        overrideMove(owner, newMove, true);
    }

    @Override
    public boolean performIntercept() {
        if (owner instanceof AbstractMonster) {
            addToBot(new SplitMonsterAction((AbstractMonster) owner));
        }
        return restoreReplacedMove(owner);
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }

    @Override
    public int splitPoint() {
        return owner.maxHealth/2;
    }
}
