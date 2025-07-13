package ChimeraMonsters.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DamagePatches {
    @SpirePatch2(clz = AbstractMonster.class, method = "damage")
    public static class RetargetDamage {
        @SpirePrefixPatch
        public static SpireReturn<Void> plz(AbstractMonster __instance, DamageInfo info) {
            if (__instance.intent != CustomIntentPatches.CHIMERA_MONSTERS_INTERCEPTING && info.type == DamageInfo.DamageType.NORMAL) {
                for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                    if (monster != __instance && !monster.isDeadOrEscaped() && monster.intent == CustomIntentPatches.CHIMERA_MONSTERS_INTERCEPTING) {
                        monster.damage(info);
                        return SpireReturn.Return();
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }
}
