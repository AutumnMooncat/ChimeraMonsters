package ChimeraMonsters.actions;

import ChimeraMonsters.util.IntentManager;
import ChimeraMonsters.util.IntentPayload;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

import java.util.function.Consumer;

public class MakeTemporaryIntentAction extends AbstractGameAction {
    private IntentPayload pl;

    public MakeTemporaryIntentAction(AbstractCreature owner, EnemyMoveInfo move, Consumer<IntentPayload> takeTurnFunc) {
        this(owner, move, null, takeTurnFunc);
    }

    public MakeTemporaryIntentAction(AbstractCreature owner, EnemyMoveInfo move, DamageInfo info, Consumer<IntentPayload> takeTurnFunc) {
        this.source = owner;
        this.target = owner;
        this.actionType = ActionType.SPECIAL;
        if (owner instanceof AbstractMonster) {
            this.pl = new IntentPayload();
            pl.owner = (AbstractMonster) owner;
            pl.damage = info;
            pl.fields.move = move;
            pl.takeTurnFunc = takeTurnFunc.andThen(thiz -> addToBot(new DoAction(() -> IntentManager.removeExtraIntent(thiz.owner, thiz))));
        } else {
            this.isDone = true;
        }
    }

    @Override
    public void update() {
        if (pl != null) {
            IntentManager.addExtraIntent(pl.owner, pl);
        }
        this.isDone = true;
    }
}
