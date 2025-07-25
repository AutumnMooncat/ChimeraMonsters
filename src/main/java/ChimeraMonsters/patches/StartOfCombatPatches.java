package ChimeraMonsters.patches;

import ChimeraMonsters.util.FightModificationManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class StartOfCombatPatches {
    @SpirePatch2(clz = MonsterGroup.class, method = "init")
    public static class RollOnCombatStart {
        @SpirePrefixPatch()
        public static void plz(MonsterGroup __instance) {
            FightModificationManager.rollFightModifiers(__instance);
        }
    }
}
