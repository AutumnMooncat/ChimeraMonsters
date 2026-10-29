package ChimeraMonsters.actions;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.function.Function;

public class TimedAction extends AbstractGameAction {
    private final Function<Float, Boolean> update;

    public TimedAction(Function<Float, Boolean> update) {
        this.source = AbstractDungeon.player;
        this.actionType = ActionType.SPECIAL;
        this.update = update;
    }

    @Override
    public void update() {
        isDone = update.apply(duration);
        duration += Gdx.graphics.getDeltaTime();
    }
}
