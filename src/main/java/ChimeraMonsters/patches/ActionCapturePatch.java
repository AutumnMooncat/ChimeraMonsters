package ChimeraMonsters.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ActionCapturePatch {
    public static final Consumer<AbstractGameAction> DO_NOTHING = a -> {};
    public static boolean doCapture;
    public static List<AbstractGameAction> capturedActions = new ArrayList<>();
    public static Consumer<AbstractGameAction> onCapture = DO_NOTHING;

    @SpirePatch2(clz = GameActionManager.class, method = "addToBottom")
    @SpirePatch2(clz = GameActionManager.class, method = "addToTop")
    public static class Capture {
        @SpirePrefixPatch
        public static SpireReturn<Void> yoink(AbstractGameAction action) {
            if (doCapture) {
                capturedActions.add(action);
                onCapture.accept(action);
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    public static void clear() {
        doCapture = false;
        onCapture = DO_NOTHING;
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
}
