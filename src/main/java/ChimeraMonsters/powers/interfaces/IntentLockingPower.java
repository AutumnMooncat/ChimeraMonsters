package ChimeraMonsters.powers.interfaces;

import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public interface IntentLockingPower {
    /**
     * Determines if the last move should remain locked.
     * If not locked, the intended move may still be replaced by interceptors.
     * @param lastMove The last move used.
     * @param intendedMove The intended move to set.
     * @return Determines if the last move should remain locked in or if the intended move can be set.
     */
    boolean shouldLock(EnemyMoveInfo lastMove, EnemyMoveInfo intendedMove);
}
