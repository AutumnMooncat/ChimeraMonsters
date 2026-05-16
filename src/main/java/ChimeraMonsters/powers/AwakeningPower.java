package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.MoveManipulationPatches;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.powers.interfaces.MonsterCantDiePower;
import com.megacrit.cardcrawl.actions.animations.ShoutAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.monsters.beyond.AwakenedOne;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.UnawakenedPower;
import com.megacrit.cardcrawl.vfx.combat.IntenseZoomEffect;

import java.util.Objects;

public class AwakeningPower extends AbstractInternalLogicPower implements MonsterCantDiePower, IntentInterceptingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(AwakeningPower.class.getSimpleName());
    private boolean activated;
    private EnemyMoveInfo lastMove;

    public AwakeningPower(AbstractCreature owner) {
        super(POWER_ID, owner, -1);
        priority = -100;
    }

    @Override
    public boolean cantDie(AbstractMonster monsterOwner) {
        return !activated;
    }

    @Override
    public void onPreventDeath(AbstractMonster monsterOwner) {
        if (!activated) {
            // TODO Check what happens if death while another interceptor is active
            monsterOwner.powers.removeIf(pow -> pow.type == PowerType.DEBUFF || Objects.equals(pow.ID, UnawakenedPower.POWER_ID) || Objects.equals(pow.ID, GainStrengthPower.POWER_ID));
            MoveManipulationPatches.removeAndResetInterceptor(monsterOwner);
            MoveManipulationPatches.applyInterceptor(monsterOwner, this, getMove(monsterOwner));
            addToBot(new ShoutAction(owner, AwakenedOne.DIALOG[0]));
        }
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        return 0;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        lastMove = replacedMove;
        overrideMove(owner, new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.UNKNOWN, -1, 0, false), true);
    }

    @Override
    public boolean performIntercept() {
        activated = true;
        owner.halfDead = false;
        addToBot(new SFXAction("VO_AWAKENEDONE_1"));
        addToBot(new VFXAction(owner, new IntenseZoomEffect(owner.hb.cX, owner.hb.cY, true), 0.05F, true));
        addToBot(new HealAction(owner, owner, owner.maxHealth));
        addToBot(new ApplyPowerAction(owner, owner, new WrathfulPower(owner)));
        setMove(owner, lastMove);
        return true;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }
}
