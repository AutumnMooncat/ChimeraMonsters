package ChimeraMonsters.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ActionCapturePatch {
    public static final Consumer<AbstractGameAction> DO_NOTHING = a -> {};
    public static final Function<AbstractGameAction, AbstractGameAction> PASS_THROUGH = a -> a;
    public static final Predicate<AbstractGameAction> TRUE = a -> true;
    public static final Predicate<AbstractGameAction> FALSE = a -> false;
    public static boolean doCapture;
    public static List<AbstractGameAction> capturedActions = new ArrayList<>();
    public static Consumer<AbstractGameAction> onCapture = DO_NOTHING;
    public static Function<AbstractGameAction, AbstractGameAction> redirect = PASS_THROUGH;
    public static Object instigator = null;
    public static boolean shouldPassInstigator = true;

    @SpirePatch2(clz = AbstractGameAction.class, method = SpirePatch.CLASS)
    public static class ActionFields {
        public static SpireField<Object> boundInstigator = new SpireField<>(() -> null);
    }

    @SpirePatch2(clz = GameActionManager.class, method = "addToBottom")
    @SpirePatch2(clz = GameActionManager.class, method = "addToTop")
    public static class Capture {
        @SpirePrefixPatch
        public static SpireReturn<Void> yoink(@ByRef AbstractGameAction[] action) {
            action[0] = redirect.apply(action[0]);
            if (action[0] == null) {
                return SpireReturn.Return();
            }
            if (instigator != null) {
                ActionFields.boundInstigator.set(action[0], instigator);
            } else if (shouldPassInstigator && AbstractDungeon.actionManager.currentAction != null) {
                ActionFields.boundInstigator.set(action[0], ActionFields.boundInstigator.get(AbstractDungeon.actionManager.currentAction));
            }
            if (doCapture) {
                capturedActions.add(action[0]);
                onCapture.accept(action[0]);
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    public static void clear() {
        doCapture = false;
        onCapture = DO_NOTHING;
        redirect = PASS_THROUGH;
        instigator = null;
        shouldPassInstigator = false;
        capturedActions.clear();
    }

    public static void releaseToBot() {
        doCapture = false;
        onCapture = DO_NOTHING;
        for (AbstractGameAction action : capturedActions) {
            AbstractDungeon.actionManager.addToBottom(action);
        }
        capturedActions.clear();
    }

    public static void releaseToTop() {
        doCapture = false;
        onCapture = DO_NOTHING;
        for (int i = capturedActions.size() - 1; i >= 0; i--) {
            AbstractDungeon.actionManager.addToTop(capturedActions.get(i));
        }
        capturedActions.clear();
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "damage")
    @SpirePatch(clz = AbstractPlayer.class, method = "onCardDrawOrDiscard")
    @SpirePatch(clz = AbstractPlayer.class, method = "draw", paramtypez = {int.class})
    @SpirePatch(clz = AbstractPlayer.class, method = "onVictory")
    @SpirePatch(clz = AbstractPlayer.class, method = "channelOrb")
    @SpirePatch(clz = AbstractMonster.class, method = "damage")
    @SpirePatch(clz = AbstractMonster.class, method = "heal")
    @SpirePatch(clz = AbstractMonster.class, method = "die", paramtypez = {boolean.class})
    @SpirePatch(clz = AbstractCreature.class, method = "heal", paramtypez = {int.class, boolean.class})
    @SpirePatch(clz = AbstractCreature.class, method = "addBlock")
    @SpirePatch(clz = AbstractCreature.class, method = "addPower")
    @SpirePatch(clz = AbstractCreature.class, method = "applyStartOfTurnPowers")
    @SpirePatch(clz = AbstractCreature.class, method = "applyTurnPowers")
    @SpirePatch(clz = AbstractCreature.class, method = "applyStartOfTurnPostDrawPowers")
    public static class DisableReactionaryActionBinding {
        @SpirePrefixPatch
        public static void disableBefore(AbstractCreature __instance) {
            shouldPassInstigator = false;
        }

        @SpirePostfixPatch
        public static void enableAfter(AbstractCreature __instance) {
            shouldPassInstigator = true;
        }
    }
}
