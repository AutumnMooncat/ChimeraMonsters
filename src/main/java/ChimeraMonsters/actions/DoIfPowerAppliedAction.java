package ChimeraMonsters.actions;

import ChimeraMonsters.patches.WasPowerActuallyAppliedPatches;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class DoIfPowerAppliedAction extends AbstractGameAction {
    ApplyPowerAction powerAction;
    AbstractGameAction appliedAction;
    AbstractGameAction negatedAction;

    public DoIfPowerAppliedAction(ApplyPowerAction powerAction, AbstractGameAction appliedAction) {
        this(powerAction, appliedAction, null);
    }

    public DoIfPowerAppliedAction(ApplyPowerAction powerAction, AbstractGameAction appliedAction, AbstractGameAction negatedAction) {
        this.powerAction = powerAction;
        this.appliedAction = appliedAction;
        this.negatedAction = negatedAction;
    }

    public DoIfPowerAppliedAction(AbstractPower power, AbstractGameAction appliedAction) {
        this (WasPowerActuallyAppliedPatches.ActionField.accompanyingAction.get(power), appliedAction);
    }

    public DoIfPowerAppliedAction(AbstractPower power, AbstractGameAction appliedAction, AbstractGameAction negatedAction) {
        this (WasPowerActuallyAppliedPatches.ActionField.accompanyingAction.get(power), appliedAction, negatedAction);
    }

    @Override
    public void update() {
        if (powerAction == null) {
            this.isDone = true;
            return;
        }
        if (WasPowerActuallyAppliedPatches.AppliedField.actuallyApplied.get(powerAction)) {
            if (appliedAction != null) {
                this.addToTop(appliedAction);
            }
        } else {
            if (negatedAction != null) {
                this.addToTop(negatedAction);
            }
        }
        this.isDone = true;
    }
}
