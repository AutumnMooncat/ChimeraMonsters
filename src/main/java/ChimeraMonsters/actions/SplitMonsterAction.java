package ChimeraMonsters.actions;

import ChimeraMonsters.util.MonsterSpawnHelper;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateShakeAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.actions.unique.CannotLoseAction;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.Collections;
import java.util.List;

public class SplitMonsterAction extends AbstractGameAction {
    public AbstractMonster mon;

    public SplitMonsterAction(AbstractMonster mon) {
        this.mon = mon;
    }

    @Override
    public void update() {
        isDone = true;
        List<AbstractMonster> splitMonsters = MonsterSpawnHelper.getSplitMonsters(mon);
        if (splitMonsters.isEmpty()) {
            return;
        }

        addToTop(new CanLoseAction());
        Collections.reverse(splitMonsters);
        for (AbstractMonster splitMonster : splitMonsters) {
            addToTop(new SpawnMonsterAction(splitMonster, false));
        }
        addToTop(new SFXAction("SLIME_SPLIT"));
        addToTop(new WaitAction(1.0F));
        addToTop(new SuicideAction(mon, false));
        addToTop(new HideHealthBarAction(mon));
        addToTop(new AnimateShakeAction(mon, 1.0F, 0.1F));
        addToTop(new CannotLoseAction());
    }
}
