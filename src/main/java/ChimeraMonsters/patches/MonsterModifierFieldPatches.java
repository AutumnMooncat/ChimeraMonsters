package ChimeraMonsters.patches;

import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
import java.util.List;

public class MonsterModifierFieldPatches {
    @SpirePatch(clz = AbstractMonster.class, method = "<class>")
    public static class ModifierFields {
        public static SpireField<Boolean> rolled = new SpireField<>(() -> false);
        public static SpireField<List<AbstractMonsterModifier>> receivedModifiers = new SpireField<>(ArrayList::new);
        public static SpireField<String> originalName = new SpireField<>(() -> null);
        public static SpireField<Float> blockMulti = new SpireField<>(() -> 1f);
        public static SpireField<IntentInterceptingPower> interceptor = new SpireField<>(() -> null);
    }
}
