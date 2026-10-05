package ChimeraMonsters.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;

@SpirePatch(clz = DamageInfo.class, method = "<class>")
public class DamageInfoFields {
    public static SpireField<Integer> lockedDamage = new SpireField<>(() -> null);
    public static SpireField<Boolean> temporalDelay = new SpireField<>(() -> false);
}
