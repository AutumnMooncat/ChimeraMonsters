package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.CustomIntentPatches;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.util.DataUtil;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class InterceptionPower extends AbstractInternalLogicPower implements IntentInterceptingPower, DataUtil {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(InterceptionPower.class.getSimpleName());
    public static final String BLOCK_KEY = POWER_ID+":didBlock";
    public int cooldown;

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
        setData(BLOCK_KEY, false);
        cooldown = 4;
        overrideMove(owner, new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.DEFEND, -1, 0, false));
    }

    @Override
    public boolean performIntercept() {
        if (getData(BLOCK_KEY)) {
            return false;
        }
        setData(BLOCK_KEY, true);
        addToBot(new GainBlockAction(owner, owner, amount));
        return false;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        if (getData(BLOCK_KEY)) {
            overrideMove(owner, new EnemyMoveInfo((byte) -1, CustomIntentPatches.CHIMERA_MONSTERS_INTERCEPTING, -1, 0, false));
            return true;
        }
        return false;
    }

    @Override
    public void onFinishedThisIntercept() {
        setData(BLOCK_KEY, false);
    }

    @Override
    public Object getHandler() {
        return getDataHandler();
    }
}
