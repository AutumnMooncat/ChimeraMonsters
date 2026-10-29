package ChimeraMonsters.orbs;

import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.damagemods.vfx.LightningVFX;
import ChimeraMonsters.patches.ActionCapturePatch;
import ChimeraMonsters.util.MonsterOrbHelper;
import ChimeraMonsters.util.PatchHelper;
import ChimeraMonsters.util.Wiz;
import ChimeraMonsters.util.analysis.ActionAnalyzer;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.BindingHelper;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

import java.util.ArrayList;

public class MonsterProxyOrb extends AbstractOrb {
    private static final ArrayList<AbstractDamageModifier> lightning = new ArrayList<AbstractDamageModifier>() {{add(new LightningVFX());}};
    public AbstractOrb proxy;

    // Lifted logic from ThiefPower, but we should never use this
    // Could be viable to do visuals this way for stuff
    public MonsterProxyOrb(AbstractOrb proxy) {
        this.proxy = proxy;
        sync();
    }

    private void sync() {
        this.baseEvokeAmount = MonsterOrbHelper.getBaseEvoke(proxy);
        this.evokeAmount = proxy.evokeAmount;
        this.basePassiveAmount = MonsterOrbHelper.getBasePassive(proxy);
        this.passiveAmount = proxy.passiveAmount;
    }

    @Override
    public void onEvoke() {
        sync();
        ActionCapturePatch.doCapture = true;
        ActionCapturePatch.instigator = evokeAmount;
        proxy.onEvoke();
        ActionCapturePatch.instigator = null;
        redirectActions();
    }

    @Override
    public void onStartOfTurn() {
        sync();
        ActionCapturePatch.doCapture = true;
        ActionCapturePatch.instigator = passiveAmount;
        proxy.onStartOfTurn();
        ActionCapturePatch.instigator = null;
        redirectActions();
    }

    @Override
    public void onEndOfTurn() {
        sync();
        ActionCapturePatch.doCapture = true;
        ActionCapturePatch.instigator = passiveAmount;
        proxy.onEndOfTurn();
        ActionCapturePatch.instigator = null;
        redirectActions();
    }

    @Override
    public void applyFocus() {
        AbstractMonster owner = MonsterOrbHelper.findOwner(this);
        if (owner == null) return;
        MonsterOrbHelper.applyFocus(owner, proxy);
        sync();
    }

    @Override
    public AbstractOrb makeCopy() {
        return new MonsterProxyOrb(proxy.makeCopy());
    }

    @Override
    public void updateDescription() {
        proxy.updateDescription();
        sync();
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        proxy.render(spriteBatch);
        sync();
    }

    @Override
    public void playChannelSFX() {
        proxy.playChannelSFX();
    }

    @Override
    public void update() {
        proxy.update();
        sync();
    }

    @Override
    public void updateAnimation() {
        super.updateAnimation();
        proxy.updateAnimation();
        proxy.cX = this.cX;
        proxy.cY = this.cY;
    }

    @Override
    public void triggerEvokeAnimation() {
        proxy.triggerEvokeAnimation();
    }

    @Override
    public void setSlot(int slotNum, int maxOrbs) {
        AbstractMonster owner = MonsterOrbHelper.findOwner(this);
        if (owner == null) return;
        MonsterOrbHelper.setSlot(owner, this, MonsterOrbHelper.getOrbs(owner).indexOf(this), MonsterOrbHelper.getMaxOrbs(owner));
        MonsterOrbHelper.setSlot(owner, proxy, MonsterOrbHelper.getOrbs(owner).indexOf(this), MonsterOrbHelper.getMaxOrbs(owner));
    }

    @Override
    protected void renderText(SpriteBatch sb) {
        ReflectionHacks.RMethod method = PatchHelper.findOverride(proxy, "renderText", SpriteBatch.class);
        if (method == null) return;
        method.invoke(proxy, sb);
    }

    private void redirectActions() {
        ArrayList<AbstractGameAction> captured = new ArrayList<>(ActionCapturePatch.capturedActions);
        ActionCapturePatch.clear();
        AbstractDungeon.actionManager.addToBottom(new DoAction(() -> {
            ActionCapturePatch.redirect = this::doRedirect;
            AbstractDungeon.actionManager.addToTop(new DoAction(() -> {
                ActionCapturePatch.redirect = ActionCapturePatch.PASS_THROUGH;
            }));
            for (int i = captured.size() - 1; i >= 0; i--) {
                AbstractDungeon.actionManager.addToTop(captured.get(i));
            }
        }));
    }

    private AbstractGameAction doRedirect(AbstractGameAction action) {
        AbstractMonster owner = MonsterOrbHelper.findOwner(this);
        if (owner == null) return action;
        action.source = owner;
        if (action.target instanceof AbstractPlayer) {
            action.target = owner;
        } else {
            action.target = Wiz.adp();
        }
        if (action instanceof ChannelAction) {
            AbstractOrb orb = ReflectionHacks.getPrivate(action, ChannelAction.class, "orbType");
            boolean auto = ReflectionHacks.getPrivate(action, ChannelAction.class, "autoEvoke");
            return new MonsterOrbHelper.MonsterChannelAction(owner, orb, true, auto);
        }
        if (ActionAnalyzer.doesDamage(action)) {
            DamageInfo info = ActionAnalyzer.findDamage(action);
            AbstractGameAction.AttackEffect effect = action.attackEffect;
            if (info != null) {
                if (effect == AbstractGameAction.AttackEffect.LIGHTNING) {
                    DamageInfo newInfo = BindingHelper.makeInfo(lightning, owner, info.base, DamageInfo.DamageType.NORMAL);
                    return new DamageAction(Wiz.adp(), newInfo, AbstractGameAction.AttackEffect.NONE);
                } else {
                    DamageInfo newInfo = new DamageInfo(owner, info.base, info.type);
                    newInfo.applyPowers(owner, Wiz.adp());
                    return new DamageAction(Wiz.adp(), newInfo, effect);
                }
            } else {
                Object o = ActionCapturePatch.ActionFields.boundInstigator.get(action);
                Integer damage = null;
                DamageInfo.DamageType type = DamageInfo.DamageType.NORMAL;
                if (o instanceof AbstractCard) {
                    DamageInfo temp = new DamageInfo(owner, ((AbstractCard) o).baseDamage, DamageInfo.DamageType.NORMAL);
                    temp.applyPowers(owner, Wiz.adp());
                    damage = temp.output;
                } else if (o instanceof Integer) {
                    damage = (Integer) o;
                    type = DamageInfo.DamageType.THORNS;
                }
                if (damage == null) {
                    return null;
                }
                if (effect == AbstractGameAction.AttackEffect.LIGHTNING) {
                    return new DamageAction(Wiz.adp(), BindingHelper.makeInfo(lightning, owner, damage, type), AbstractGameAction.AttackEffect.NONE);
                } else {
                    return new DamageAction(Wiz.adp(), new DamageInfo(owner, damage, type), effect);
                }
            }
        }
        return action;
    }
}
