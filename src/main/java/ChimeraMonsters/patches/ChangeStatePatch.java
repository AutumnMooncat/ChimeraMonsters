package ChimeraMonsters.patches;

import com.evacipated.cardcrawl.mod.stslib.actions.common.StunMonsterAction;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.animations.AnimateHopAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;

public class ChangeStatePatch {
    @SpirePatch2(clz = ChangeStateAction.class, method = "update")
    public static class StunCheck {
        @SpirePostfixPatch
        public static void plz(ChangeStateAction __instance, AbstractMonster ___m, String ___stateName) {
            if (__instance.isDone && ___stateName.equals("ARMOR_BREAK") && ___m.powers.stream().noneMatch(p -> p instanceof PlatedArmorPower) && MonsterFields.stunOnArmorBreak.get(___m)) {
                MonsterFields.stunOnArmorBreak.set(___m, false);
                AbstractDungeon.actionManager.addToBottom(new AnimateHopAction(___m));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom(new AnimateHopAction(___m));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3F));
                AbstractDungeon.actionManager.addToBottom(new AnimateHopAction(___m));
                AbstractDungeon.actionManager.addToBottom(new StunMonsterAction(___m, ___m));
            }
        }
    }
}
