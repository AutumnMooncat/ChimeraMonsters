package ChimeraMonsters.patches;

import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.powers.interfaces.IntentLockingPower;
import ChimeraMonsters.util.Wiz;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
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
        IntentInterceptingPower interceptor = MonsterModifierFieldPatches.ModifierFields.interceptor.get(monster);
        if (interceptor != null) {
            if (interceptor.setFollowupInterceptionIntent()) {
                return true;
            } else {
                MonsterModifierFieldPatches.ModifierFields.interceptor.set(monster, null);
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
                    interceptor.setInterceptIntent(intendedMove);
                    MonsterModifierFieldPatches.ModifierFields.interceptor.set(monster, interceptor);
                    newInterceptor = interceptor;
                }
            }
        }
        return newInterceptor != null;
    }

    public static boolean shouldSetMove(AbstractMonster monster, EnemyMoveInfo lastMove, EnemyMoveInfo intendedMove) {
        if (hasIntentLock(monster, lastMove, intendedMove)) {
            return false;
        }
        if (hasInterceptorFollowup(monster)) {
            return false;
        }
        return !hasNewInterceptor(monster, intendedMove);
    }

    public static boolean performedIntercept(AbstractMonster monster) {
        IntentInterceptingPower interceptor = MonsterModifierFieldPatches.ModifierFields.interceptor.get(monster);
        if (interceptor != null) {
            if (!interceptor.performIntercept()) {
                Wiz.atb(new RollMoveAction(monster));
            }
            return true;
        }
        return false;
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
        setMove(creature, info, false);
    }

    public static void setMove(AbstractCreature creature, EnemyMoveInfo info, boolean instantCreate) {
        if (creature instanceof AbstractMonster) {
            byte moveByte = ((AbstractMonster) creature).nextMove;
            try {
               if(info.nextMove!=-1){
                   ((AbstractMonster) creature).moveHistory.add(info.nextMove);
                   //TODO: MoveName
               }
                moveField.set(creature, info);
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
            boolean shouldContinue = shouldSetMove(__instance, lastMove, intendedMove);
            EnemyMoveInfo currentInfo = getMove(__instance);
            if (currentInfo == lastMove) {
                currentInfo = intendedMove;
            }
            for (AbstractPower power : __instance.powers) {
                if (power instanceof IntentInterceptingPower) {
                    IntentInterceptingPower interceptor = (IntentInterceptingPower) power;
                    if (interceptor != MonsterModifierFieldPatches.ModifierFields.interceptor.get(__instance)) {
                        interceptor.otherIntentPicked(currentInfo);
                    }
                }
            }
            return shouldContinue ? SpireReturn.Continue() : SpireReturn.Return();
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
                        m.replace(String.format("if (!%s.performedIntercept(m)) {$_ = $proceed($$);}", MoveManipulationPatches.class.getName()));
                    }
                }
            };
        }
    }
}
