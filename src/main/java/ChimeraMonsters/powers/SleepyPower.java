package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.EnemyMoveInfoFields;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class SleepyPower extends AbstractInternalLogicPower implements IntentInterceptingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(SleepyPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public int cooldown;

    public SleepyPower(AbstractCreature owner, int amount) {
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
        if (owner.currentHealth == owner.maxHealth) {
            return 0f;
        }
        return cooldown == 0 ? 1f : 0f;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        EnemyMoveInfo info = new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.BUFF, -1, 0, false);
        EnemyMoveInfoFields.name.set(info, powerStrings.DESCRIPTIONS[0]);
        overrideMove(owner, info);
    }

    @Override
    public boolean performIntercept() {
        cooldown = 3;
        addToBot(new HealAction(owner, owner, amount));
        return false;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }
}
