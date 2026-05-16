package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.CustomIntentPatches;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class InterceptionPower extends AbstractInternalLogicPower implements IntentInterceptingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(InterceptionPower.class.getSimpleName());
    public int cooldown;
    public boolean didBlock;

    public InterceptionPower(AbstractCreature owner, int amount) {
        super(POWER_ID, owner, amount);
    }

    @Override
    public void otherIntentPicked(EnemyMoveInfo nextMove) {
        if (cooldown > 0) {
            cooldown--;
        }
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        if (AbstractDungeon.getMonsters().monsters.stream().noneMatch(mon -> mon != owner && !mon.isDeadOrEscaped())) {
            return 0f;
        }
        return cooldown == 0 ? 1f : 0f;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        didBlock = false;
        overrideMove(owner, new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.DEFEND, -1, 0, false));
    }

    @Override
    public boolean performIntercept() {
        if (!didBlock) {
            didBlock = true;
            cooldown = 4;
            addToBot(new GainBlockAction(owner, owner, amount));
        }
        return false;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        if (didBlock) {
            overrideMove(owner, new EnemyMoveInfo((byte) -1, CustomIntentPatches.CHIMERA_MONSTERS_INTERCEPTING, -1, 0, false));
            return true;
        }
        return false;
    }

    @Override
    public void onFinishedThisIntercept() {
        didBlock = false;
    }
}
