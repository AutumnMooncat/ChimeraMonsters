package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.ActionCapturePatch;
import ChimeraMonsters.patches.CustomIntentPatches;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.util.Wiz;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;

public class DoppelPower extends AbstractInternalLogicPower implements IntentInterceptingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(DoppelPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public DoppelPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, owner, amount);
        priority = -5;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        return intendedMove.baseDamage>-1 ? 1 : 0;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        int multi = replacedMove.multiplier == 0 ? 2 : replacedMove.multiplier*2;
        EnemyMoveInfo newMove = new EnemyMoveInfo(replacedMove.nextMove, replacedMove.intent, replacedMove.baseDamage,multi, true);
        setMove(owner,newMove);
    }

    @Override
    public boolean performIntercept() {
        if (owner instanceof AbstractMonster) {
            AbstractMonster ownerMon = (AbstractMonster) owner;
            ActionCapturePatch.doCapture = true;
            ownerMon.takeTurn();
            ArrayList<AbstractGameAction> captured = new ArrayList<>(ActionCapturePatch.capturedActions);
            ActionCapturePatch.clear();
            for (AbstractGameAction action : captured) {
                if (action instanceof DamageAction && action.target == AbstractDungeon.player) {
                    DamageInfo di = ReflectionHacks.getPrivate(action, DamageAction.class, "info");
                    addToBot(new DamageAction(Wiz.adp(), di, action.attackEffect));
                    addToBot(new DamageAction(Wiz.adp(), di, action.attackEffect));
                } else if (action instanceof VampireDamageAction && action.target == AbstractDungeon.player) {
                    DamageInfo di = ReflectionHacks.getPrivate(action, VampireDamageAction.class, "info");
                    addToBot(new VampireDamageAction(Wiz.adp(), di, action.attackEffect));
                    addToBot(new VampireDamageAction(Wiz.adp(), di, action.attackEffect));
                } else {
                    if(action instanceof RollMoveAction){
                        ChimeraMonstersMod.logger.log(Level.WARN, "AAAAAAAAAAH");
                    }
                    addToBot(action);
                }
            }
        }
        return true;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }

    private void changeIntent(EnemyMoveInfo replacedMove, boolean instant) {
        if (replacedMove.intent == AbstractMonster.Intent.ATTACK) {
            setMove(owner, new EnemyMoveInfo(replacedMove.nextMove, CustomIntentPatches.CHIMERA_MONSTERS_SWEEPING_ATTACK, replacedMove.baseDamage, replacedMove.multiplier, replacedMove.isMultiDamage), instant);
        } else if (replacedMove.intent == AbstractMonster.Intent.ATTACK_BUFF) {
            setMove(owner, new EnemyMoveInfo(replacedMove.nextMove, CustomIntentPatches.CHIMERA_MONSTERS_SWEEPING_ATTACK_BUFF, replacedMove.baseDamage, replacedMove.multiplier, replacedMove.isMultiDamage), instant);
        } else if (replacedMove.intent == AbstractMonster.Intent.ATTACK_DEBUFF) {
            setMove(owner, new EnemyMoveInfo(replacedMove.nextMove, CustomIntentPatches.CHIMERA_MONSTERS_SWEEPING_ATTACK_DEBUFF, replacedMove.baseDamage, replacedMove.multiplier, replacedMove.isMultiDamage), instant);
        } else {
            setMove(owner, new EnemyMoveInfo(replacedMove.nextMove, CustomIntentPatches.CHIMERA_MONSTERS_SWEEPING_ATTACK_BLOCK, replacedMove.baseDamage, replacedMove.multiplier, replacedMove.isMultiDamage), instant);
        }
    }
}
