package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.util.Wiz;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class ResupplyPower extends AbstractInternalLogicPower implements IntentInterceptingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(ResupplyPower.class.getSimpleName());
    private final int block, str;
    private boolean primed, activated;

    public ResupplyPower(AbstractCreature owner, int block, int str) {
        super(POWER_ID, owner, -1);
        this.block = block;
        this.str = str;
        priority = -5;
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        return primed || activated ? 0 : 1;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        EnemyMoveInfo newMove = new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.BUFF, 0, 0, false);
        overrideMove(owner, newMove);
        primed = true;
    }

    @Override
    public boolean performIntercept() {
        Wiz.forAllMonstersLiving(mon -> {
            addToBot(new GainBlockAction(mon, owner, block));
            addToBot(new ApplyPowerAction(mon, owner, new StrengthPower(mon, str)));
        });

        activated = true;
        return restoreReplacedMove(owner);
    }

    @Override
    public void onFinishedThisIntercept() {
        primed = false;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }
}
