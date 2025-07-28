package ChimeraMonsters.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class LifestealDamage extends AbstractDamageModifier {
    public int percentage;


    public LifestealDamage(int percentage){
        this.percentage=percentage;
    }

    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (lastDamageTaken > 0) {
            addToTop(new HealAction(info.owner, info.owner, lastDamageTaken*percentage/100));
        }
    }

    @Override
    public boolean isInherent() {
        return true;
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new LifestealDamage(percentage);
    }
}
