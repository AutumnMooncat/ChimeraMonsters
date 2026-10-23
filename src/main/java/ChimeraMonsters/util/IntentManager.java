package ChimeraMonsters.util;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.IntentUpdatePatches;
import ChimeraMonsters.patches.MonsterFields;
import ChimeraMonsters.patches.MoveManipulationPatches;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class IntentManager {
    private static final String replacedIntendedMove = ChimeraMonstersMod.makeID(IntentManager.class.getSimpleName()+":replacedIntendedMove");
    private static final String interceptor = ChimeraMonstersMod.makeID(IntentManager.class.getSimpleName()+":interceptor");

    public static IntentPayload payloadFromBaseIntent(AbstractMonster monster) {
        IntentPayload pl = new IntentPayload();
        pl.owner = monster;
        pl.damage = null;
        pl.readFields(monster);
        return pl;
    }

    public static void backupMainIntent(AbstractMonster monster) {
        MonsterFields.mainIntent.set(monster, payloadFromBaseIntent(monster));
    }

    public static void restoreMainIntent(AbstractMonster monster) {
        IntentPayload main = MonsterFields.mainIntent.get(monster);
        if (main == null) return;
        main.writeFields(monster);
        MonsterFields.mainIntent.set(monster, null);
    }

    public static void setActivePayload(AbstractMonster monster, IntentPayload pl) {
        MonsterFields.activeIntent.set(monster, pl);
        pl.writeFields(monster);
    }

    public static void releaseActivePayload(AbstractMonster monster, IntentPayload pl) {
        pl.readFields(monster);
        MonsterFields.activeIntent.set(monster, null);
    }

    public static boolean hasExtraIntents(AbstractMonster monster) {
        return !extraIntents(monster).isEmpty();
    }

    public static ArrayList<IntentPayload> extraIntents(AbstractMonster monster) {
        return MonsterFields.extraIntents.get(monster);
    }

    public static void addExtraIntent(AbstractMonster monster, IntentPayload pl) {
        addExtraIntent(monster, pl, false);
    }

    public static void addExtraIntent(AbstractMonster monster, IntentPayload pl, boolean andRoll) {
        backupMainIntent(monster);
        setActivePayload(monster, pl);
        if (andRoll) {
            monster.rollMove();
        }
        IntentUpdatePatches.PlzMoveHitbox.needsRefresh = true;
        monster.refreshIntentHbLocation();
        IntentUpdatePatches.PlzMoveHitbox.needsRefresh = false;
        monster.createIntent();
        IntentManager.extraIntents(monster).add(pl);
        if (pl.damage != null) {
            monster.damage.add(pl.damage);
        }
        releaseActivePayload(monster, pl);
        restoreMainIntent(monster);
        if (MoveManipulationPatches.getMove(monster) != null)
        {
            // TODO Handle battle setup better, may be other problems
            monster.applyPowers();
        }
    }

    public static void removeExtraIntent(AbstractMonster monster, IntentPayload pl) {
        IntentManager.extraIntents(monster).remove(pl);
        if (pl.damage != null) {
            monster.damage.remove(pl.damage);
        }
    }

    public static IntentPayload getActiveIntent(AbstractMonster monster) {
        return MonsterFields.activeIntent.get(monster);
    }

    public static Object getDataHandler(AbstractMonster monster) {
        IntentPayload pl = getActiveIntent(monster);
        if (pl != null) {
            return pl;
        }
        return monster;
    }

    public static IntentInterceptingPower getInterceptor(AbstractMonster monster) {
        IntentPayload pl = getActiveIntent(monster);
        if (pl != null) {
            if (pl.interceptor != null) {
                return pl.interceptor;
            }
            return null;
        }
        return MonsterFields.interceptor.get(monster);
    }

    public static EnemyMoveInfo getReplacedMove(AbstractMonster monster) {
        IntentPayload pl = getActiveIntent(monster);
        if (pl != null) {
            return pl.replacedIntendedMove;
        }
        return MonsterFields.replacedIntendedMove.get(monster);
    }

    public static void applyInterceptor(AbstractMonster monster, IntentInterceptingPower interceptor, EnemyMoveInfo intendedMove) {
        IntentPayload pl = getActiveIntent(monster);
        if (pl != null) {
            pl.replacedIntendedMove = intendedMove;
            pl.interceptor = interceptor;
        } else {
            MonsterFields.replacedIntendedMove.set(monster, intendedMove);
            MonsterFields.interceptor.set(monster, interceptor);
        }
        interceptor.setInterceptIntent(intendedMove);
        monster.moveName = interceptor.interceptName();
    }

    public static void removeAndResetInterceptor(AbstractMonster monster) {
        IntentInterceptingPower interceptor = getInterceptor(monster);
        if (interceptor != null) {
            interceptor.onFinishedThisIntercept();
        }
        // Don't actually clear out the replaced intended move as we could still use it for backup
        IntentPayload pl = getActiveIntent(monster);
        if (pl != null) {
            pl.interceptor = null;
        } else {
            MonsterFields.interceptor.set(monster, null);
        }
    }

    public static void organizeIntentOrder(AbstractMonster monster) {
        if (!hasExtraIntents(monster)) return;
        IntentPayload current = payloadFromBaseIntent(monster);
        ArrayList<IntentPayload> all = new ArrayList<>();
        all.add(current);
        all.addAll(extraIntents(monster));
        Collections.sort(all);
        IntentPayload first = all.remove(0);
        if (current != first) {
            IntentInterceptingPower currentInterceptor = MonsterFields.interceptor.get(monster);
            EnemyMoveInfo currentReplacedMove = MonsterFields.replacedIntendedMove.get(monster);
            HashMap<String, Object> currentData = DataUtil.getDataMap(monster);
            MonsterFields.interceptor.set(monster, first.interceptor);
            MonsterFields.replacedIntendedMove.set(monster, first.replacedIntendedMove);
            DataUtil.setDataMap(monster, DataUtil.getDataMap(first));
            current.interceptor = currentInterceptor;
            current.replacedIntendedMove = currentReplacedMove;
            DataUtil.setDataMap(current, currentData);
            first.writeFields(monster);
            extraIntents(monster).clear();
            extraIntents(monster).addAll(all);
        }
    }

    public static ReflectionHacks.RMethod updateIntentVFXMethod(AbstractMonster mon) {
        return ReflectionHacks.privateMethod(AbstractMonster.class, "updateIntentVFX");
    }

    public static ReflectionHacks.RMethod renderIntentMethod(AbstractMonster mon) {
        if (mon instanceof CustomMonster) {
            return ReflectionHacks.privateMethod(CustomMonster.class, "renderIntent", SpriteBatch.class);
        }
        return ReflectionHacks.privateMethod(AbstractMonster.class, "renderIntent", SpriteBatch.class);
    }

    public static ReflectionHacks.RMethod renderDamageRangeMethod(AbstractMonster mon) {
        if (mon instanceof CustomMonster) {
            return ReflectionHacks.privateMethod(CustomMonster.class, "renderDamageRange", SpriteBatch.class);
        }
        return ReflectionHacks.privateMethod(AbstractMonster.class, "renderDamageRange", SpriteBatch.class);
    }
}
