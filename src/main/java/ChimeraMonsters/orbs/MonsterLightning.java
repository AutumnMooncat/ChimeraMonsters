package ChimeraMonsters.orbs;

import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.util.MonsterOrbHelper;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Lightning;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;
import com.megacrit.cardcrawl.vfx.combat.OrbFlareEffect;

public class MonsterLightning extends Lightning {
    @Override
    public void onEvoke() {
        AbstractMonster owner = MonsterOrbHelper.findOwner(this);
        if (owner == null) return;
        DamageInfo info = new DamageInfo(owner, evokeAmount, DamageInfo.DamageType.THORNS);
        AbstractDungeon.actionManager.addToTop(new DoAction(() -> {
            info.output = AbstractOrb.applyLockOn(AbstractDungeon.player, info.base);
            AbstractDungeon.actionManager.addToTop(new DamageAction(AbstractDungeon.player, info, AbstractGameAction.AttackEffect.NONE, true));
            AbstractDungeon.actionManager.addToTop(new VFXAction(new LightningEffect(AbstractDungeon.player.drawX, AbstractDungeon.player.drawY), 0f));
            AbstractDungeon.actionManager.addToTop(new SFXAction("ORB_LIGHTNING_EVOKE"));
        }));
    }

    @Override
    public void onEndOfTurn() {
        AbstractMonster owner = MonsterOrbHelper.findOwner(this);
        if (owner == null) return;
        DamageInfo info = new DamageInfo(owner, passiveAmount, DamageInfo.DamageType.THORNS);
        AbstractDungeon.actionManager.addToBottom(new DoAction(() -> {
            info.output = AbstractOrb.applyLockOn(AbstractDungeon.player, info.base);
            AbstractDungeon.actionManager.addToTop(new DamageAction(AbstractDungeon.player, info, AbstractGameAction.AttackEffect.NONE, true));
            AbstractDungeon.actionManager.addToTop(new VFXAction(new LightningEffect(AbstractDungeon.player.drawX, AbstractDungeon.player.drawY), 0f));
            AbstractDungeon.actionManager.addToTop(new SFXAction("ORB_LIGHTNING_EVOKE"));
            AbstractDungeon.actionManager.addToTop(new VFXAction(new OrbFlareEffect(this, OrbFlareEffect.OrbFlareColor.LIGHTNING), 0f));
        }));
    }

    @Override
    public AbstractOrb makeCopy() {
        return new MonsterLightning();
    }

    @Override
    public void applyFocus() {
        AbstractMonster owner = MonsterOrbHelper.findOwner(this);
        if (owner == null) return;
        MonsterOrbHelper.applyFocus(owner, this);
    }
}
