package ChimeraMonsters.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.function.Supplier;

public class DoIfAction extends AbstractGameAction {
    private Supplier<Boolean> check;
    private Runnable doIf;
    private Runnable doIfNot;

    public DoIfAction(Supplier<Boolean> check, Runnable doIf) {
        this(check, doIf, null);
    }

    public DoIfAction(Supplier<Boolean> check, Runnable doIf, Runnable doIfNot) {
        this.source = AbstractDungeon.player;
        this.actionType = ActionType.SPECIAL;
        this.check = check;
        this.doIf = doIf;
        this.doIfNot = doIfNot;
    }

    @Override
    public void update() {
        if (check.get()) {
            doIf.run();
        } else if (doIfNot != null) {
            doIfNot.run();
        }
        this.isDone = true;
    }
}
