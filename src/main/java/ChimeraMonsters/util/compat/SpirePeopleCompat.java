package ChimeraMonsters.util.compat;

import ChimeraMonsters.ChimeraMonstersMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.lang.reflect.Field;

public class SpirePeopleCompat {
    public static boolean asplode;

    public static void fixName(AbstractMonster monster) {
        if (asplode) {
            return;
        }

        try {
            Field field = Class.forName("spirepeople.patches.MonsterColorDataPatch").getField("baseName");
            SpireField<String> baseName = (SpireField<String>) field.get(null);
            baseName.set(monster, monster.name);
        } catch (Exception ignored) {
             if (!asplode) {
                 ChimeraMonstersMod.logger.error("Spire People compat failed", ignored);
                 asplode = true;
             }
        }
    }
}
