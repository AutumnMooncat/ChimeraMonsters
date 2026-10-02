package ChimeraMonsters.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

@SpirePatch(clz = MonsterGroup.class, method = "<class>")
public class MonsterGroupFields {
    public static SpireField<Boolean> rolledModifiers = new SpireField<>(() -> false);
    public static SpireField<String> encounterID = new SpireField<>(() -> "");
    public static SpireField<String> fightName = new SpireField<>(() -> "");
}
