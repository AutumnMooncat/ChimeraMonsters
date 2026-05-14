package ChimeraMonsters.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class EnemyMoveInfoPatches {
    @SpirePatch(clz = EnemyMoveInfo.class, method = "<class>")
    public static class Fields {
        public static SpireField<String> name = new SpireField<>(() -> null);
    }

    public static void setName(EnemyMoveInfo info, String name) {
        Fields.name.set(info, name);
    }

    public static String getName(EnemyMoveInfo info) {
        return Fields.name.get(info);
    }
}
