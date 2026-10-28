package ChimeraMonsters.orbs;

import ChimeraMonsters.util.MonsterOrbHelper;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Frost;
import com.megacrit.cardcrawl.vfx.combat.OrbFlareEffect;

public class MonsterFrost extends Frost {
    @Override
    public void onEvoke() {
        AbstractMonster owner = MonsterOrbHelper.findOwner(this);
        if (owner == null) return;
        AbstractDungeon.actionManager.addToTop(new GainBlockAction(owner, owner, evokeAmount));
    }

    @Override
    public void onEndOfTurn() {
        AbstractMonster owner = MonsterOrbHelper.findOwner(this);
        if (owner == null) return;
        AbstractDungeon.actionManager.addToBottom(new VFXAction(new OrbFlareEffect(this, OrbFlareEffect.OrbFlareColor.FROST), 0f));
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(owner, owner, passiveAmount, true));
    }

    @Override
    public AbstractOrb makeCopy() {
        return new MonsterFrost();
    }

    @Override
    public void applyFocus() {
        AbstractMonster owner = MonsterOrbHelper.findOwner(this);
        if (owner == null) return;
        MonsterOrbHelper.applyFocus(owner, this);
    }
}
