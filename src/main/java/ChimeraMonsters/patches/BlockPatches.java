package ChimeraMonsters.patches;

import ChimeraMonsters.powers.AbstractModifierPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class BlockPatches {
    @SpirePatch2(clz = GainBlockAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez = { AbstractCreature.class, int.class })
    @SpirePatch2(clz = GainBlockAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez = { AbstractCreature.class, AbstractCreature.class, int.class })
    public static class ChangeBlock {
        @SpirePostfixPatch
        public static void plz(GainBlockAction __instance) {
            if (__instance.target instanceof AbstractMonster) {
                float temp = __instance.amount;
                for (AbstractPower power : __instance.target.powers) {
                    if (power instanceof AbstractModifierPower) {
                        temp = ((AbstractModifierPower) power).modifyMonsterBlock(temp);
                    }
                }
                temp *= MonsterModifierFieldPatches.ModifierFields.blockMulti.get(__instance.target);
                __instance.amount = (int) (temp);
            }
        }
    }
}
