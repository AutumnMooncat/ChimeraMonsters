package ChimeraMonsters.patches;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.powers.interfaces.IntentLockingPower;
import ChimeraMonsters.util.DataUtil;
import ChimeraMonsters.util.IntentManager;
import ChimeraMonsters.util.IntentPayload;
import ChimeraMonsters.util.Wiz;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.IntentFlashAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.ShowMoveNameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.lang.reflect.Field;

public class MoveManipulationPatches {
    private static final Field moveField;
    private static boolean wasRolled;
    private static boolean changingState;
    private static boolean takeTurnLock;

    static {
        try {
            moveField = AbstractMonster.class.getDeclaredField("move");
            moveField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasIntentLock(AbstractMonster monster, EnemyMoveInfo lastMove, EnemyMoveInfo intendedMove) {
        for (AbstractPower power : monster.powers) {
            if (power instanceof IntentLockingPower) {
                if (((IntentLockingPower) power).shouldLock(lastMove, intendedMove)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasInterceptorFollowup(AbstractMonster monster) {
        IntentInterceptingPower interceptor = IntentManager.getInterceptor(monster);
        if (interceptor != null) {
            if (interceptor.setFollowupInterceptionIntent()) {
                return true;
            } else {
                removeAndResetInterceptor(monster);
            }
        }
        return false;
    }

    public static boolean hasNewInterceptor(AbstractMonster monster, EnemyMoveInfo intendedMove) {
        IntentInterceptingPower newInterceptor = null;
        for (AbstractPower power : monster.powers) {
            if (power instanceof IntentInterceptingPower) {
                IntentInterceptingPower interceptor = (IntentInterceptingPower) power;
                if (newInterceptor == null && (!((IntentInterceptingPower) power).rollOnly() || wasRolled) && AbstractDungeon.aiRng.random(1f) <= interceptor.interceptRate(intendedMove)) {
                    applyInterceptor(monster, interceptor, intendedMove);
                    newInterceptor = interceptor;
                }
            }
        }
        return newInterceptor != null;
    }

    public static boolean shouldSetMove(AbstractMonster monster, EnemyMoveInfo lastMove, EnemyMoveInfo intendedMove) {
        // Some enemies have important intent state changes like Bryd/Parasite stun, Guardian close up, Hexaghost inferno
        if (changingState) {
            removeAndResetInterceptor(monster);
            return true;
        }
        if (hasIntentLock(monster, lastMove, intendedMove)) {
            return false;
        }
        if (hasInterceptorFollowup(monster)) {
            return false;
        }
        return !hasNewInterceptor(monster, intendedMove);
    }

    public static void applyInterceptor(AbstractMonster monster, IntentInterceptingPower interceptor, EnemyMoveInfo intendedMove) {
        IntentManager.applyInterceptor(monster, interceptor, intendedMove);
    }

    public static void removeAndResetInterceptor(AbstractMonster monster) {
        IntentManager.removeAndResetInterceptor(monster);
    }

    // Called by AbstractMonsterDynamicPatch, should wrap all stateChange overrides and damage overrides if reaction changes intent
    public static void beginStateChange(AbstractMonster monster) {
        changingState = true;
    }

    public static void endStateChange(AbstractMonster monster) {
        changingState = false;
    }

    public static boolean performedPayload(AbstractMonster monster) {
        if (takeTurnLock) {
            return false;
        }
        IntentPayload pl = IntentManager.getActiveIntent(monster);
        if (pl != null) {
            takeTurnLock = true;
            pl.takeTurn();
            takeTurnLock = false;
            return true;
        }
        return false;
    }

    // Called by GetNextAction
    public static boolean performedIntercept(AbstractMonster monster) {
        IntentInterceptingPower interceptor = IntentManager.getInterceptor(monster);
        if (interceptor != null) {
            if (!interceptor.performIntercept()) {
                rollOrBackupMove(monster);
            }
            return true;
        }
        return false;
    }

    // Called by GetNextAction
    public static void doExtraIntents(AbstractMonster monster) {
        IntentManager.backupMainIntent(monster);
        for (IntentPayload pl : IntentManager.extraIntents(monster)) {
            IntentManager.setActivePayload(monster, pl);
            if (pl.fields.intent != AbstractMonster.Intent.NONE) {
                AbstractDungeon.actionManager.addToBottom(new ShowMoveNameAction(monster));
                //AbstractDungeon.actionManager.addToBottom(new IntentFlashAction(m));
                AbstractDungeon.actionManager.addToBottom(new DoAction(() -> {
                    IntentManager.backupMainIntent(monster);
                    IntentManager.setActivePayload(monster, pl);
                    monster.flashIntent();
                    IntentManager.releaseActivePayload(monster, pl);
                    IntentManager.restoreMainIntent(monster);
                }));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(Settings.ACTION_DUR_MED));
            }
            if (!performedIntercept(monster)) {
                monster.takeTurn();
            }
            IntentManager.releaseActivePayload(monster, pl);
        }
        IntentManager.restoreMainIntent(monster);
    }

    public static void rollOrBackupMove(AbstractMonster monster) {
        IntentPayload pl = IntentManager.getActiveIntent(monster);
        // There might have been a good reason we used 3 actions, but 1 is less likely to break
        Wiz.atb(new DoAction(() -> {
            MonsterFields.successfullyRolledMove.set(monster, false);
            if (pl != null) {
                IntentManager.backupMainIntent(monster);
                IntentManager.setActivePayload(monster, pl);
            }
            monster.rollMove();
            boolean needsBackup = !MonsterFields.successfullyRolledMove.get(monster);
            if (pl != null) {
                if (needsBackup) {
                    EnemyMoveInfo backup = pl.replacedIntendedMove;
                    if (backup != null) {
                        overrideMove(monster, backup);
                    }
                    // Else super rip
                }
                IntentManager.releaseActivePayload(monster, pl);
                IntentManager.restoreMainIntent(monster);
            } else if (needsBackup) {
                EnemyMoveInfo backup = MonsterFields.replacedIntendedMove.get(monster);
                if (backup != null) {
                    overrideMove(monster, backup);
                }
                // Else rip lol
            }
        }));
    }

    public static EnemyMoveInfo getMove(AbstractCreature creature) {
        if (creature instanceof AbstractMonster) {
            try {
                return (EnemyMoveInfo) moveField.get(creature);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static void setMove(AbstractCreature creature, EnemyMoveInfo info) {
        if (creature instanceof AbstractMonster) {
            try {
                moveField.set(creature, info);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void overrideMove(AbstractCreature creature, EnemyMoveInfo info) {
        overrideMove(creature, info, false);
    }

    public static void overrideMove(AbstractCreature creature, EnemyMoveInfo info, boolean instantCreate) {
        if (creature instanceof AbstractMonster) {
            byte moveByte = ((AbstractMonster) creature).nextMove;
            try {
               if (moveByte != -1) {
                   ((AbstractMonster) creature).moveHistory.add(moveByte);
               }

               moveField.set(creature, info);
               ((AbstractMonster) creature).moveName = EnemyMoveInfoFields.name.get(info);
               if (instantCreate) {
                   ((AbstractMonster) creature).createIntent();
               }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "setMove", paramtypez = {String.class, byte.class, AbstractMonster.Intent.class, int.class, int.class, boolean.class})
    public static class ThisIsVeryJank {
        @SpirePrefixPatch
        public static SpireReturn<Void> plz(AbstractMonster __instance, String moveName, byte nextMove, AbstractMonster.Intent intent, int baseDamage, int multiplier, boolean isMultiDamage) {
            EnemyMoveInfo lastMove = getMove(__instance);
            EnemyMoveInfo intendedMove = new EnemyMoveInfo(nextMove, intent, baseDamage, multiplier, isMultiDamage);
            MonsterFields.successfullyRolledMove.set(__instance, true);
            EnemyMoveInfoFields.name.set(intendedMove, moveName);
            boolean shouldContinue = shouldSetMove(__instance, lastMove, intendedMove);
            EnemyMoveInfo currentInfo = getMove(__instance);
            if (currentInfo == lastMove) {
                currentInfo = intendedMove;
            }
            for (AbstractPower power : __instance.powers) {
                if (power instanceof IntentInterceptingPower) {
                    IntentInterceptingPower interceptor = (IntentInterceptingPower) power;
                    if (interceptor != IntentManager.getInterceptor(__instance)) {
                        interceptor.otherIntentPicked(currentInfo);
                    }
                }
            }
            return shouldContinue ? SpireReturn.Continue() : SpireReturn.Return();
        }

        @SpirePostfixPatch
        public static void setName(AbstractMonster __instance) {
            EnemyMoveInfo currentMove = getMove(__instance);
            if (currentMove == null) {
                // Throw if this happens because it means we messed something up
                // Will have to prevent if we find out modded monsters do this on purpose for whatever reason
                ChimeraMonstersMod.logger.error("Monster {} left setMove with null EnemyMoveInfo", __instance.getClass().getSimpleName());
                throw new IllegalStateException("Monster "+__instance.getClass().getSimpleName()+" left setMove with null EnemyMoveInfo");
            }
            EnemyMoveInfoFields.name.set(currentMove, __instance.moveName);
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "createIntent")
    public static class OnIntentCreated {
        @SpirePrefixPatch
        public static void plz (AbstractMonster __instance) {
            IntentInterceptingPower interceptor = IntentManager.getInterceptor(__instance);
            if (interceptor != null) {
                interceptor.onInterceptedIntentCreated();
            }
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "rollMove")
    public static class RollMove {
        @SpirePrefixPatch
        public static void setRolled(AbstractMonster __instance) {
            wasRolled = true;
        }

        @SpirePostfixPatch
        public static void unsetRolled(AbstractMonster __instance) {
            wasRolled = false;
        }
    }

    @SpirePatch(clz = GameActionManager.class, method = "getNextAction")
    public static class GetNextAction {
        @SpireInstrumentPatch
        public static ExprEditor plz() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractMonster.class.getName()) && m.getMethodName().equals("takeTurn")) {
                        m.replace(String.format("if (!%1$s.performedIntercept(m)) {$_ = $proceed($$);} %1$s.doExtraIntents(m);", MoveManipulationPatches.class.getName()));
                    }
                }
            };
        }
    }

    @SpirePatch(clz = RollMoveAction.class, method = SpirePatch.CONSTRUCTOR)
    public static class GrabIntentRef {
        public static final String REF_KEY = ChimeraMonstersMod.makeID(GrabIntentRef.class.getSimpleName()+":refPayload");

        @SpirePostfixPatch
        public static void plz(RollMoveAction __instance, AbstractMonster monster) {
            IntentPayload pl = IntentManager.getActiveIntent(monster);
            if (pl != null) {
                DataUtil.setData(__instance, REF_KEY, pl);
            }
        }
    }

    @SpirePatch(clz = RollMoveAction.class, method = "update")
    public static class UseIntentRef {
        @SpirePrefixPatch
        public static void apply(RollMoveAction __instance, AbstractMonster ___monster) {
            IntentPayload pl = DataUtil.getData(__instance, GrabIntentRef.REF_KEY);
            if (pl != null) {
                IntentManager.backupMainIntent(___monster);
                IntentManager.setActivePayload(___monster, pl);
            }
        }

        @SpirePostfixPatch
        public static void restore(RollMoveAction __instance, AbstractMonster ___monster) {
            IntentPayload pl = DataUtil.getData(__instance, GrabIntentRef.REF_KEY);
            if (pl != null) {
                IntentManager.releaseActivePayload(___monster, pl);
                IntentManager.restoreMainIntent(___monster);
                DataUtil.removeData(__instance, GrabIntentRef.REF_KEY);
            }
        }
    }
}
