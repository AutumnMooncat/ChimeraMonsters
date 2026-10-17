package ChimeraMonsters.util;

import ChimeraMonsters.patches.IntentUpdatePatches;
import ChimeraMonsters.patches.MonsterFields;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
import java.util.HashMap;

public class IntentManager {
    public static IntentPayload payloadFromBaseIntent(AbstractMonster monster) {
        IntentPayload pl = new IntentPayload();
        pl.owner = monster;
        pl.handler = monster;
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
        MonsterFields.extraIntents.get(monster).add(pl);
        if (pl.damage != null) {
            monster.damage.add(pl.damage);
        }
        releaseActivePayload(monster, pl);
        restoreMainIntent(monster);
        monster.applyPowers();
    }

    public static void removeExtraIntent(AbstractMonster monster, IntentPayload pl) {
        MonsterFields.extraIntents.get(monster).remove(pl);
        if (pl.damage != null) {
            monster.damage.remove(pl.damage);
        }
    }

    public static Object getDataHandler(AbstractMonster monster) {
        IntentPayload pl = MonsterFields.activeIntent.get(monster);
        if (pl != null) {
            return pl;
        }
        return monster;
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
