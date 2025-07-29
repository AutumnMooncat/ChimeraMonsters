package ChimeraMonsters.patches;

import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.helpers.MonsterHelper.GetEncounter;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class MonsterEncounterPatches {

    @SpirePatch2(clz = GetEncounter.class, method = "Postfix")
    public static class ThanksPapaKio
    {
        @SpirePostfixPatch
        public static void plsWork(Object[] __args){
            MonsterGroup mg = (MonsterGroup) __args[0];
            String key = (String) __args[1];
            MonsterEncounterIDFields.encounterField.set(mg, key);

        }
    }

    @SpirePatch2(clz = MonsterGroup.class, method = SpirePatch.CLASS)
    public static class MonsterEncounterIDFields{
        public static SpireField<String> encounterField = new SpireField<>(() -> "");
    }
}
