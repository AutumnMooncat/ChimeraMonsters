package ChimeraMonsters.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;

public class WasPowerActuallyAppliedPatches {

    @SpirePatch(clz = ApplyPowerAction.class, method = SpirePatch.CLASS)
    public static class AppliedField {
        public static SpireField<Boolean> actuallyApplied = new SpireField<>(() -> false);
    }

    @SpirePatch(clz = AbstractPower.class, method = SpirePatch.CLASS)
    public static class ActionField {
        public static SpireField<ApplyPowerAction> accompanyingAction = new SpireField<>(() -> null);
    }

    @SpirePatch2(clz = ApplyPowerAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCreature.class, AbstractCreature.class, AbstractPower.class, int.class, boolean.class, AbstractGameAction.AttackEffect.class})
    public static class LinkAction {
        @SpirePostfixPatch
        public static void link(ApplyPowerAction __instance, AbstractPower ___powerToApply) {
            ActionField.accompanyingAction.set(___powerToApply, __instance);
        }
    }

    @SpirePatch2(clz = ApplyPowerAction.class, method = "update")
    public static class UpdateActuallyApplied {
        @SpireInsertPatch(locator = Locator.class)
        public static void setApplied(ApplyPowerAction __instance, AbstractPower ___powerToApply) {
            AppliedField.actuallyApplied.set(__instance, true);
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r instanceof OnActuallyApplyPowerRelic) {
                    ((OnActuallyApplyPowerRelic) r).onApplyPower(__instance.target, __instance.source, ___powerToApply);
                }
            }
        }
    }

    public static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher matcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "onModifyPower");
            return LineFinder.findAllInOrder(ctBehavior, matcher);
        }
    }

    public interface OnActuallyApplyPowerRelic {
        void onApplyPower(AbstractCreature target, AbstractCreature source, AbstractPower power);
    }
}
