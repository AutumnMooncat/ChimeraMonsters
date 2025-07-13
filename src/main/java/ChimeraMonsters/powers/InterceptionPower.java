package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.CustomIntentPatches;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class InterceptionPower extends AbstractEasyPower implements IntentInterceptingPower, InvisiblePower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(InterceptionPower.class.getSimpleName());
    public int cooldown;
    public boolean didBlock;

    public InterceptionPower(AbstractCreature owner, int amount) {
        super(POWER_ID, POWER_ID, NeutralPowertypePatch.NEUTRAL, false, owner, amount);
    }

    @Override
    public void atEndOfRound() {
        if (cooldown > 0) {
            cooldown--;
        }
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        return cooldown == 0 ? 1f : 0f;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        didBlock = false;
        setMove(owner, new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.DEFEND, -1, 0, false));
    }

    @Override
    public boolean performIntercept() {
        if (!didBlock) {
            didBlock = true;
            cooldown = 4;
            addToBot(new GainBlockAction(owner, owner, amount));
        } else {
            didBlock = false;
        }
        return false;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        if (didBlock) {
            setMove(owner, new EnemyMoveInfo((byte) -1, CustomIntentPatches.CHIMERA_MONSTERS_INTERCEPTING, -1, 0, false));
            return true;
        }
        return false;
    }
}
