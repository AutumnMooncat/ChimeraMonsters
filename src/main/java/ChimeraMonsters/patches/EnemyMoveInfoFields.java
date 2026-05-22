package ChimeraMonsters.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

@SpirePatch(clz = EnemyMoveInfo.class, method = "<class>")
public class EnemyMoveInfoFields {
    public static SpireField<String> name = new SpireField<>(() -> null);
}
