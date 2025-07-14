package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.patches.ActionCapturePatch;
import ChimeraMonsters.patches.CustomIntentPatches;
import ChimeraMonsters.patches.MonsterModifierFieldPatches;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.util.PowerAnalyzer;
import ChimeraMonsters.util.Wiz;
import basemod.ReflectionHacks;
import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import java.util.ArrayList;

public class BerserkerPower extends AbstractEasyPower implements IntentInterceptingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(BerserkerPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean activated;

    public BerserkerPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        priority = -5;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        boolean wasActivated = activated;
        activated = (owner.currentHealth - damageAmount) <= owner.maxHealth/2;
        if (!wasActivated && activated && owner instanceof AbstractMonster) {
            flash();
            addToBot(new ApplyPowerAction(owner, owner, new StrengthPower(owner, amount)));
            addToBot(new DoAction(() -> {
                changeIntent(getMove(owner), true);
                MonsterModifierFieldPatches.ModifierFields.interceptor.set(owner, this);
            }));
        }
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        if (activated) {
            if (intendedMove.intent == AbstractMonster.Intent.ATTACK || intendedMove.intent == AbstractMonster.Intent.ATTACK_BUFF || intendedMove.intent == AbstractMonster.Intent.ATTACK_DEBUFF || intendedMove.intent == AbstractMonster.Intent.ATTACK_DEFEND) {
                return 1f;
            }
        }
        return 0f;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        changeIntent(replacedMove, false);
    }

    @Override
    public boolean performIntercept() {
        if (owner instanceof AbstractMonster) {
            AbstractMonster ownerMon = (AbstractMonster) owner;
            ActionCapturePatch.doCapture = true;
            ownerMon.takeTurn();
            ArrayList<AbstractGameAction> captured = new ArrayList<>(ActionCapturePatch.capturedActions);
            Wiz.forAllMonstersLiving(mon -> {
                if (mon != owner) {
                    addToBot(new AnimateSlowAttackAction(owner));
                    for (AbstractGameAction action : captured) {
                        if (action instanceof DamageAction && action.target == AbstractDungeon.player) {
                            DamageInfo di = ReflectionHacks.getPrivate(action, DamageAction.class, "info");
                            addToBot(new DamageAction(mon, new DamageInfo(owner, monsterToMonsterDamage(owner, mon, di.base)), action.attackEffect));
                        } else if (action instanceof VampireDamageAction && action.target == AbstractDungeon.player) {
                            DamageInfo di = ReflectionHacks.getPrivate(action, VampireDamageAction.class, "info");
                            addToBot(new VampireDamageAction(mon, new DamageInfo(owner, monsterToMonsterDamage(owner, mon, di.base)), action.attackEffect));
                        } else if (action instanceof ApplyPowerAction && action.target == AbstractDungeon.player) {
                            AbstractPower pow = ReflectionHacks.getPrivate(action, ApplyPowerAction.class, "powerToApply");
                            if (PowerAnalyzer.safeToApply(pow)) {
                                AbstractPower toApply = PowerAnalyzer.tryGetReplacement(pow, mon, owner, pow.amount);
                                if (toApply == null) {
                                    if (pow instanceof CloneablePowerInterface) {
                                        toApply = ((CloneablePowerInterface) pow).makeCopy();
                                        toApply.owner = mon;
                                    } else {
                                        toApply = PowerAnalyzer.tryGetClone(pow, mon, owner, pow.amount);
                                    }
                                }
                                if (toApply != null) {
                                    addToBot(new ApplyPowerAction(mon, owner, toApply));
                                }
                            }
                        }
                    }
                }
            });
            ActionCapturePatch.releaseToBot();
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
