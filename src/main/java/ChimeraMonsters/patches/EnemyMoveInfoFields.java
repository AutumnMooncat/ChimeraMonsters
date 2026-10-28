package ChimeraMonsters.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

@SpirePatch(clz = EnemyMoveInfo.class, method = "<class>")
public class EnemyMoveInfoFields {
    public static SpireField<String> name = new SpireField<>(() -> null);
    public static SpireField<Integer> channels = new SpireField<>(() -> 0);
    public static SpireField<Integer> evokes = new SpireField<>(() -> 0);
}
