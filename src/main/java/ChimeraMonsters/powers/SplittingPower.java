package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.patches.ActionCapturePatch;
import ChimeraMonsters.patches.MonsterEncounterPatches;
import ChimeraMonsters.patches.MonsterModifierFieldPatches;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.util.MonsterSpawnHelper;
import com.megacrit.cardcrawl.actions.animations.AnimateShakeAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.actions.unique.CannotLoseAction;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

import java.util.ArrayList;

public class SplittingPower extends AbstractEasyPower implements IntentInterceptingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(SplittingPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean activated;

    public SplittingPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
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
                EnemyMoveInfo newMove = new EnemyMoveInfo(getMove(owner).nextMove, AbstractMonster.Intent.UNKNOWN, -1 , 0, false);
                setMove(owner,newMove, true);
                MonsterModifierFieldPatches.ModifierFields.interceptor.set(owner, this);
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
        setMove(owner,newMove);
    }

    @Override
    public boolean performIntercept() {
        if (owner instanceof AbstractMonster) {
            AbstractDungeon.actionManager.addToBottom(new CannotLoseAction());
            AbstractDungeon.actionManager.addToBottom(new AnimateShakeAction(owner, 1.0F, 0.1F));
            AbstractDungeon.actionManager.addToBottom(new HideHealthBarAction(owner));
            AbstractDungeon.actionManager.addToBottom(new SuicideAction((AbstractMonster) owner, false));
            AbstractDungeon.actionManager.addToBottom(new WaitAction(1.0F));
            AbstractDungeon.actionManager.addToBottom(new SFXAction("SLIME_SPLIT"));

            ArrayList<AbstractMonster> splitMonsters = MonsterSpawnHelper.getSplitMonsters((AbstractMonster) owner, MonsterEncounterPatches.MonsterEncounterIDFields.encounterField.get(AbstractDungeon.getCurrRoom().monsters));
            for (AbstractMonster splitMonster : splitMonsters) {
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(splitMonster, false));
            }
            AbstractDungeon.actionManager.addToBottom(new CanLoseAction());
            ActionCapturePatch.clear();
        }
        return true;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }
}
