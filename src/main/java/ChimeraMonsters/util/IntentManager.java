package ChimeraMonsters.util;

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

public class IntentManager {

    public static IntentPayload payloadFromBaseIntent(AbstractMonster monster) {
        IntentPayload pl = new IntentPayload();
        pl.owner = monster;
        pl.damage = null;
        pl.interceptor = MonsterFields.interceptor.get(monster);
        pl.replacedIntendedMove = MonsterFields.replacedIntendedMove.get(monster);
        pl.readFields(monster);
        return pl;
    }

    public static IntentPayload getMainIntent(AbstractMonster monster) {
        return MonsterFields.mainIntent.get(monster);
    }

    public static void checkStoreMainIntent(AbstractMonster monster) {
        IntentPayload main = getMainIntent(monster);
        if (main == null)
        {
            main = payloadFromBaseIntent(monster);
            DataUtil.setDataMap(main, DataUtil.getDataMap(monster));
            MonsterFields.mainIntent.set(monster, main);
            MonsterFields.activeIntent.set(monster, main);
            MonsterFields.interceptor.set(monster, null);
            MonsterFields.replacedIntendedMove.set(monster, null);
        }
    }

    public static void checkReleaseMainIntent(AbstractMonster monster) {
        if (hasExtraIntents(monster)) return;
        IntentPayload main = getMainIntent(monster);
        main.writeFields(monster);
        DataUtil.getDataMap(monster).putAll(DataUtil.getDataMap(main));
        MonsterFields.mainIntent.set(monster, null);
        MonsterFields.activeIntent.set(monster, null);
        MonsterFields.interceptor.set(monster, main.interceptor);
        MonsterFields.replacedIntendedMove.set(monster, main.replacedIntendedMove);
    }

    public static void writeMainPayload(AbstractMonster monster) {
        writePayload(monster, getMainIntent(monster));
    }

    public static void readMainPayload(AbstractMonster monster) {
        readPayload(monster, getMainIntent(monster));
    }

    public static void writePayload(AbstractMonster monster, IntentPayload pl) {
        MonsterFields.activeIntent.set(monster, pl);
        pl.writeFields(monster);
    }

    public static void readPayload(AbstractMonster monster, IntentPayload pl) {
        pl.readFields(monster);
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
        addExtraIntent(monster, pl, andRoll, false);
    }

    public static void addExtraIntent(AbstractMonster monster, IntentPayload pl, boolean andRoll, boolean andCreate) {
        checkStoreMainIntent(monster);
        readMainPayload(monster);
        writePayload(monster, pl);
        if (andRoll) {
            monster.rollMove();
        }
        IntentUpdatePatches.PlzMoveHitbox.needsRefresh = true;
        monster.refreshIntentHbLocation();
        IntentUpdatePatches.PlzMoveHitbox.needsRefresh = false;
        if (andCreate)
        {
            monster.createIntent();
        }
        IntentManager.extraIntents(monster).add(pl);
        if (pl.damage != null) {
            monster.damage.add(pl.damage);
        }
        readPayload(monster, pl);
        writeMainPayload(monster);
        if (MoveManipulationPatches.getMove(monster) != null)
        {
            // TODO Handle battle setup better, may be other problems
            monster.applyPowers();
        }
    }

    public static void removeExtraIntent(AbstractMonster monster, IntentPayload pl) {
        if (pl == getMainIntent(monster)) {
            MonsterFields.mainIntent.set(monster, extraIntents(monster).remove(0));
        } else {
            extraIntents(monster).remove(pl);
        }
        if (pl.damage != null) {
            monster.damage.remove(pl.damage);
        }
        checkReleaseMainIntent(monster);
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
            return pl.interceptor;
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
        IntentPayload current = getMainIntent(monster);
        ArrayList<IntentPayload> all = new ArrayList<>();
        all.add(current);
        all.addAll(extraIntents(monster));
        Collections.sort(all);
        IntentPayload first = all.remove(0);
        if (current != first) {
            readPayload(monster, current);
            MonsterFields.mainIntent.set(monster, first);
            writeMainPayload(monster);
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
