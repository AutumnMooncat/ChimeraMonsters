package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.util.Wiz;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.ThornsPower;

public class SpikyPower extends AbstractInternalLogicPower implements IntentInterceptingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(SpikyPower.class.getSimpleName());
    public int cooldown;
    private EnemyMoveInfo replacedMove;

    public SpikyPower(AbstractCreature owner, int amount) {
        super(POWER_ID, owner, amount);
        cooldown = 1;
    }

    @Override
    public void otherIntentPicked(EnemyMoveInfo nextMove) {
        if (cooldown > 0) {
            cooldown--;
        }
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        return cooldown == 0 ? 1/3f : 0;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        setMove(owner, new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.BUFF, -1, 0, false));
        this.replacedMove = replacedMove;
    }

    @Override
    public boolean performIntercept() {
        cooldown = 1;
        Wiz.atb(new ApplyPowerAction(owner, owner, new ThornsPower(owner, amount)));
        setMove(owner, replacedMove);
        replacedMove = null;
        return true;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }
}
