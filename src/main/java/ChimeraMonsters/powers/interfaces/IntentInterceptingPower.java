package ChimeraMonsters.powers.interfaces;

import ChimeraMonsters.patches.EnemyMoveInfoPatches;
import ChimeraMonsters.patches.MoveManipulationPatches;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.blights.Spear;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;

public interface IntentInterceptingPower {
    /**
     * Checked before a new move is either rolled or set.
     * Will not be checked if something has locked the intent or if a different interceptor is actively spanning multiple turns.
     * If this intercept spans multiple turns, it will continue intercepting without checking the rate again.
     * @param intendedMove EnemyMoveInfo that will be set if not intercepted.
     * @return Probability this will intercept, 0.5f will intercept 50% of the time.
     */
    float interceptRate(EnemyMoveInfo intendedMove);

    /**
     * Called when this intercepts with the intention of assigning new move info as the monster still has its move info from the last turn.
     * If this intercept spans multiple turns, this is only called on the first turn.
     * As interceptor logic has already been processed and handed over, {@link IntentInterceptingPower#overrideMove(AbstractCreature, EnemyMoveInfo)} should be used for this.
     * @see IntentInterceptingPower#setFollowupInterceptionIntent
     * @param replacedMove EnemyMoveInfo that was originally going to be set.
     */
    void setInterceptIntent(EnemyMoveInfo replacedMove);

    /**
     * Called instead of AbstractMonster.takeTurn() if this intercept is active.
     * If this intercept spans multiple turns, this does not need to return true as the next intercept stage is handled via {@link IntentInterceptingPower#setFollowupInterceptionIntent}.
     * @return If the monster already has its next move setup handled
     */
    boolean performIntercept();

    /**
     * Called on subsequent turns if this intercept was active with the intention of assigning new move into if this intercept is to continue.
     * As interceptor logic has already been processed and handed over, {@link IntentInterceptingPower#overrideMove(AbstractCreature, EnemyMoveInfo)} should be used for this.
     * @return If this intercept should continue
     */
    boolean setFollowupInterceptionIntent();

    /**
     * Called whenever this intercept is removed, either because it was finished or forced to stop.
     * Any cleanup/reset required if this interrupt is forced to stop should be done here.
     */
    default void onFinishedThisIntercept() {}

    /**
     * Called whenever this doesnt perform an intercept, including if this is due to the intent being locked or another interceptor spanning multiple turns.
     * @param nextMove The move info that the monster now has.
     */
    default void otherIntentPicked(EnemyMoveInfo nextMove) {}

    /**
     * Called when the monster actually creates and begins rendering its intent at the start of the turn
     */
    default void onInterceptedIntentCreated() {}

    /**
     * Determines if this interceptor can replace hardcoded move changes or if it only replaces randomly rolled moves
     * @return If this interceptor only replaces random rolls.
     */
    default boolean rollOnly() {
        return false;
    }

    /**
     * Allows changing the move name displayed when this intent is activated
     * @return The new move name, or null to not display one
     */
    default String interceptName() {
        return null;
    }

    /**
     * Get the private move info if the creature is a monster.
     * @param creature The creature to check.
     * @return The current move info or null if the creature is not a monster.
     */
    default EnemyMoveInfo getMove(AbstractCreature creature) {
        return MoveManipulationPatches.getMove(creature);
    }

    /**
     * Calls monster.setMove if the creature is a monster, allowing this interceptor to reset if it is done and potentially obtain a new one.
     * @see IntentInterceptingPower#setMove(AbstractCreature, String, byte, AbstractMonster.Intent, int, int, boolean)
     * @param creature The creature to try setting a move for.
     * @param info The info to unpack for the intent.
     */
    default void setMove(AbstractCreature creature, EnemyMoveInfo info) {
        setMove(creature, EnemyMoveInfoPatches.getName(info), info.nextMove, info.intent, info.baseDamage, info.multiplier, info.isMultiDamage);
    }

    /**
     * Calls monster.setMove if the creature is a monster, allowing this interceptor to reset if it is done and potentially obtain a new one.
     * @see IntentInterceptingPower#setMove(AbstractCreature, EnemyMoveInfo)
     * @param creature The creature to try setting a move for.
     * @param name The name of the move, or null to not display one.
     * @param nextMove The byte corresponding to the desired move in monster.takeTurn.
     * @param intent The intent to render.
     * @param baseDamage The base damage for the intent.
     * @param multiplier The multihit of the intent, if isMultiDamage.
     * @param isMultiDamage If multiplier should be used.
     */
    default void setMove(AbstractCreature creature, String name, byte nextMove, AbstractMonster.Intent intent, int baseDamage, int multiplier, boolean isMultiDamage) {
        if (creature instanceof AbstractMonster) {
            ((AbstractMonster) creature).setMove(name, nextMove, intent, baseDamage, multiplier, isMultiDamage);
        }
    }

    /**
     * Directly sets the private move info field if the creature is a monster.
     * This bypasses any interceptor checks/resets so the expected use case is in {@link IntentInterceptingPower#setInterceptIntent(EnemyMoveInfo)} and {@link IntentInterceptingPower#setFollowupInterceptionIntent()} as the interceptor logic has already been processed and has handed control over.
     * As such, care should be taken to manually release any interceptors if using this elsewhere.
     * @see IntentInterceptingPower#overrideMove(AbstractCreature, EnemyMoveInfo, boolean)
     * @param creature The creature to try setting the info for.
     * @param info The new move info to set.
     */
    default void overrideMove(AbstractCreature creature, EnemyMoveInfo info) {
        overrideMove(creature, info, false);
    }

    /**
     * Directly sets the private move info field and potentially immediately calls create intent if the creature is a monster.
     * If the intent is being changed while an intent is already active, create intent should be called, otherwise it should not to allow the intent to be created alongside the other intents at the start of turn.
     * This bypasses any interceptor checks/resets so the expected use case is in {@link IntentInterceptingPower#setInterceptIntent(EnemyMoveInfo)} and {@link IntentInterceptingPower#setFollowupInterceptionIntent()} as the interceptor logic has already been processed and has handed control over.
     * As such, care should be taken to manually release any interceptors if using this elsewhere.
     * @see IntentInterceptingPower#overrideMove(AbstractCreature, EnemyMoveInfo)
     * @param creature The creature to try setting the info for.
     * @param info The new move info to set.
     * @param instantCreate If create intent should be called.
     */
    default void overrideMove(AbstractCreature creature, EnemyMoveInfo info, boolean instantCreate) {
        MoveManipulationPatches.overrideMove(creature, info, instantCreate);
    }

    /**
     * Helper function for calculating damage one monster should deal to another instead of DamageInfo.applyPowers() which factors in player stances.
     * @param source The source of the damage.
     * @param target The target of the damage.
     * @param dmg The base damage.
     * @return The calculated damage to deal.
     */
    default int monsterToMonsterDamage(AbstractCreature source, AbstractCreature target, int dmg) {
        float tmp = (float)dmg;
        if (Settings.isEndless && AbstractDungeon.player.hasBlight(Spear.ID)) {
            float mod = AbstractDungeon.player.getBlight(Spear.ID).effectFloat();
            tmp *= mod;
        }

        for (AbstractPower power : source.powers) {
            tmp = power.atDamageGive(tmp, DamageInfo.DamageType.NORMAL);
        }
        for (AbstractPower power : target.powers) {
            tmp = power.atDamageReceive(tmp, DamageInfo.DamageType.NORMAL);
        }
        for (AbstractPower power : source.powers) {
            tmp = power.atDamageFinalGive(tmp, DamageInfo.DamageType.NORMAL);
        }
        for (AbstractPower power : target.powers) {
            tmp = power.atDamageFinalReceive(tmp, DamageInfo.DamageType.NORMAL);
        }

        dmg = MathUtils.floor(tmp);
        if (dmg < 0) {
            dmg = 0;
        }

        return dmg;
    }
}
