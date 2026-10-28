package ChimeraMonsters.orbs;

import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.util.MonsterOrbHelper;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Dark;

public class MonsterDark extends Dark {
    @Override
    public void onEvoke() {
        AbstractMonster owner = MonsterOrbHelper.findOwner(this);
        if (owner == null) return;
        DamageInfo info = new DamageInfo(owner, evokeAmount, DamageInfo.DamageType.THORNS);
        AbstractDungeon.actionManager.addToTop(new DoAction(() -> {
            info.output = AbstractOrb.applyLockOn(AbstractDungeon.player, info.base);
            AbstractDungeon.actionManager.addToTop(new DamageAction(AbstractDungeon.player, info, AbstractGameAction.AttackEffect.FIRE, true));
        }));
    }

    @Override
    public AbstractOrb makeCopy() {
        return new MonsterDark();
    }

    @Override
    public void applyFocus() {
        AbstractMonster owner = MonsterOrbHelper.findOwner(this);
        if (owner == null) return;
        MonsterOrbHelper.applyFocus(owner, this);
    }
}
