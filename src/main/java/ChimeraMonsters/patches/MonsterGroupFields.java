package ChimeraMonsters.patches;

import ChimeraMonsters.modifiers.groups.AbstractMonsterGroupModifier;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import java.util.ArrayList;
import java.util.List;

@SpirePatch(clz = MonsterGroup.class, method = "<class>")
public class MonsterGroupFields {
    public static SpireField<Boolean> rolledModifiers = new SpireField<>(() -> false);
    public static SpireField<List<AbstractMonsterGroupModifier>> receivedModifiers = new SpireField<>(ArrayList::new);
    public static SpireField<String> encounterID = new SpireField<>(() -> "");
    public static SpireField<String> fightName = new SpireField<>(() -> "");
}
