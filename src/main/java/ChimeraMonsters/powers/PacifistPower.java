package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.CustomIntentPatches;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.util.Wiz;
import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class PacifistPower extends AbstractInternalLogicPower implements IntentInterceptingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(PacifistPower.class.getSimpleName());
    public int cooldown;
    private EnemyMoveInfo replacedMove;

    public PacifistPower(AbstractCreature owner, int amount) {
        super(POWER_ID, owner, amount);
        cooldown = 1;
    }

    @Override
    public void otherIntentPicked(EnemyMoveInfo nextMove) {
        if (cooldown > 0 && nextMove.baseDamage > 0) {
            cooldown--;
        }
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        if (AbstractDungeon.getMonsters().monsters.stream().noneMatch(mon -> !mon.isDeadOrEscaped() && mon.currentHealth < mon.maxHealth)) {
            return 0;
        }
        if (intendedMove.baseDamage <= 0) {
            return 0;
        }
        return cooldown == 0 ? 1 : 0;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        setMove(owner, new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.BUFF, -1, 0, false));
        this.replacedMove = replacedMove;
    }

    @Override
    public void onInterceptedIntentCreated() {
        addToBot(new ApplyPowerAction(owner, owner, new InterruptablePower(owner, replacedMove)));
        replacedMove = null;
    }

    @Override
    public boolean performIntercept() {
        cooldown = 1;
        Wiz.forAllMonstersLiving(mon -> Wiz.atb(new HealAction(mon, owner, amount)));
        return false;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }
}
