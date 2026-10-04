package ChimeraMonsters.patches;

import ChimeraMonsters.powers.interfaces.MonsterBlockChangingPower;
import ChimeraMonsters.powers.interfaces.TurnStartBlockLossPower;
import basemod.patches.com.megacrit.cardcrawl.actions.GameActionManager.OnPlayerLoseBlockToggle;
import basemod.patches.com.megacrit.cardcrawl.core.AbstractCreature.ModifyPlayerLoseBlock;
import com.evacipated.cardcrawl.mod.stslib.patches.RetainMonsterBlockPatches;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
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
                    if (power instanceof MonsterBlockChangingPower) {
                        temp = ((MonsterBlockChangingPower) power).modifyMonsterBlock(temp);
                    }
                }
                temp *= MonsterFields.blockMulti.get(__instance.target);
                __instance.amount = (int) (temp);
            }
        }
    }

    @SpirePatch2(clz = AbstractCreature.class, method = "loseBlock", paramtypez = {int.class, boolean.class})
    public static class LoseBlock {
        @SpirePrefixPatch
        public static void plz(AbstractCreature __instance, int amount, boolean noAnimation) {
            if (OnPlayerLoseBlockToggle.isEnabled && __instance instanceof AbstractPlayer) {
                for (AbstractPower power : __instance.powers) {
                    if (power instanceof TurnStartBlockLossPower) {
                        ((TurnStartBlockLossPower) power).preBlockLoss();
                    }
                }
            }
            if (RetainMonsterBlockPatches.monsterStartOfTurn && __instance instanceof AbstractMonster) {
                for (AbstractPower power : __instance.powers) {
                    if (power instanceof TurnStartBlockLossPower) {
                        ((TurnStartBlockLossPower) power).preBlockLoss();
                    }
                }
            }
        }

        @SpirePostfixPatch
        public static void plz2(AbstractCreature __instance, int amount, boolean noAnimation) {
            if (OnPlayerLoseBlockToggle.isEnabled && __instance instanceof AbstractPlayer) {
                for (AbstractPower power : __instance.powers) {
                    if (power instanceof TurnStartBlockLossPower) {
                        ((TurnStartBlockLossPower) power).postBlockLoss();
                    }
                }
            }
            if (RetainMonsterBlockPatches.monsterStartOfTurn && __instance instanceof AbstractMonster) {
                for (AbstractPower power : __instance.powers) {
                    if (power instanceof TurnStartBlockLossPower) {
                        ((TurnStartBlockLossPower) power).postBlockLoss();
                    }
                }
            }
        }
    }
}
