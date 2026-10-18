package ChimeraMonsters.patches;

import ChimeraMonsters.util.IntentManager;
import ChimeraMonsters.util.IntentPayload;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class IntentUpdatePatches {
    @SpirePatch2(clz = AbstractMonster.class, method = "updateIntent")
    public static class IntentUpdate {
        @SpirePrefixPatch
        public static void extraUpdates(AbstractMonster __instance) {
            IntentManager.organizeIntentOrder(__instance);
            IntentManager.backupMainIntent(__instance);
            for (IntentPayload pl : IntentManager.extraIntents(__instance)) {
                IntentManager.setActivePayload(__instance, pl);
                pl.update();
                IntentManager.releaseActivePayload(__instance, pl);
            }
            IntentManager.restoreMainIntent(__instance);
        }
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "updateIntentVFX")
    public static class IntentVFXUpdate {
        public static boolean lock;

        @SpirePostfixPatch
        public static void extraVFXUpdates(AbstractMonster __instance) {
            if (!lock && IntentManager.hasExtraIntents(__instance)) {
                lock = true;
                IntentManager.backupMainIntent(__instance);
                ReflectionHacks.RMethod update = IntentManager.updateIntentVFXMethod(__instance);
                for (IntentPayload pl : IntentManager.extraIntents(__instance)) {
                    IntentManager.setActivePayload(__instance, pl);
                    update.invoke(__instance);
                    IntentManager.releaseActivePayload(__instance, pl);
                }
                IntentManager.restoreMainIntent(__instance);
                lock = false;
            }
        }
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "refreshIntentHbLocation")
    public static class PlzMoveHitbox {
        public static boolean needsRefresh;

        @SpirePrefixPatch
        public static SpireReturn<?> plz(AbstractMonster __instance) {
            if (needsRefresh || !IntentManager.hasExtraIntents(__instance)) return SpireReturn.Continue();
            float targetX = __instance.hb.cX + __instance.intentOffsetX;
            float targetY = __instance.hb.cY + __instance.hb_h/2f + 64f*Settings.scale/2f;
            int extras = IntentManager.extraIntents(__instance).size();
            int index = 0;
            stepHitbox(__instance.intentHb, targetX, extras, index);
            __instance.intentHb.moveY(targetY);
            for (IntentPayload pl : IntentManager.extraIntents(__instance)) {
                index++;
                stepHitbox(pl.fields.intentHb, targetX, extras, index);
                pl.fields.intentHb.moveY(targetY);
            }
            return SpireReturn.Return();
        }

        private static void stepHitbox(Hitbox hb, float origin, int others, int index) {
            float targetX = origin - others/2f * IntentPayload.SPACING + index * IntentPayload.SPACING;
            if (Math.abs(targetX - hb.cX) <= 1f) {
                hb.moveX(targetX);
            } else {
                hb.moveX(hb.cX + (targetX - hb.cX)/IntentPayload.MOVESTEP);
            }
        }
    }

    @SpirePatch2(clz = MonsterGroup.class, method = "showIntent")
    public static class ShowExtraIntents {
        @SpirePostfixPatch
        public static void plz(MonsterGroup __instance) {
            for (AbstractMonster monster : __instance.monsters) {
                if (!IntentManager.hasExtraIntents(monster)) continue;
                IntentManager.backupMainIntent(monster);
                for (IntentPayload pl : IntentManager.extraIntents(monster)) {
                    IntentManager.setActivePayload(monster, pl);
                    monster.createIntent();
                    IntentManager.releaseActivePayload(monster, pl);
                }
                IntentManager.restoreMainIntent(monster);
            }
        }
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "applyPowers")
    public static class UpdateExtraIntents {
        public static boolean lock;

        @SpirePrefixPatch
        public static void plz(AbstractMonster __instance) {
            if (lock || !IntentManager.hasExtraIntents(__instance)) return;
            lock = true;
            IntentManager.backupMainIntent(__instance);
            for (IntentPayload pl : IntentManager.extraIntents(__instance)) {
                IntentManager.setActivePayload(__instance, pl);
                __instance.applyPowers();
                IntentManager.releaseActivePayload(__instance, pl);
            }
            IntentManager.restoreMainIntent(__instance);
            lock = false;
        }
    }
}
