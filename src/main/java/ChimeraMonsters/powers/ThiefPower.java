package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.damagemods.vfx.LightningVFX;
import ChimeraMonsters.patches.ActionCapturePatch;
import ChimeraMonsters.patches.MonsterModifierFieldPatches;
import ChimeraMonsters.ui.HoveringCardManager;
import ChimeraMonsters.util.analysis.ActionAnalyzer;
import ChimeraMonsters.util.MonsterOrbHelper;
import ChimeraMonsters.util.Wiz;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.BindingHelper;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.actions.utility.ShowCardAndPoofAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

import java.util.ArrayList;

public class ThiefPower extends AbstractInternalLogicPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(ThiefPower.class.getSimpleName());
    private static final ArrayList<AbstractDamageModifier> lightning = new ArrayList<AbstractDamageModifier>() {{add(new LightningVFX());}};
    public HoveringCardManager manager;

    public ThiefPower(AbstractCreature owner, int amount) {
        super(POWER_ID, owner, amount);
        priority = -5;
        if (owner instanceof AbstractMonster) {
            manager = new HoveringCardManager(owner);
            MonsterModifierFieldPatches.ModifierFields.cardManager.set(owner, manager);
        }
    }

    @Override
    public void atStartOfTurn() {
        if (owner instanceof AbstractMonster) {
            ActionCapturePatch.doCapture = true;
            MonsterOrbHelper.applyStartOfTurnOrbs((AbstractMonster) owner);
            playNextCard();
            redirectActions();
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (owner instanceof AbstractMonster) {
            ActionCapturePatch.doCapture = true;
            MonsterOrbHelper.applyEndOfTurnOrbs((AbstractMonster) owner);
            redirectActions();
        }
    }

    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster m) {
        AbstractCard copy = card.makeStatEquivalentCopy();
        manager.addCard(copy);
    }

    private void playNextCard() {
        if (owner instanceof AbstractMonster) {
            AbstractMonster ownerMon = (AbstractMonster) owner;
            if (!manager.cards.isEmpty()) {
                AbstractCard next = manager.cards.group.remove(0);
                Wiz.adp().limbo.addToTop(next);
                next.target_x = Settings.WIDTH / 2f;
                next.target_y = Settings.HEIGHT / 2f;
                next.targetDrawScale = 1f;
                next.freeToPlayOnce = true;
                addToBot(new AnimateFastAttackAction(owner));
                addToBot(new ShowCardAndPoofAction(next));
                ActionCapturePatch.instigator = next;
                next.use(Wiz.adp(), ownerMon);
                ActionCapturePatch.instigator = null;
            }
        }
    }

    private void redirectActions() {
        ArrayList<AbstractGameAction> captured = new ArrayList<>(ActionCapturePatch.capturedActions);
        ActionCapturePatch.clear();
        addToBot(new DoAction(() -> {
            ActionCapturePatch.redirect = this::doRedirect;
            addToTop(new DoAction(() -> {
                ActionCapturePatch.redirect = ActionCapturePatch.PASS_THROUGH;
            }));
            for (int i = captured.size() - 1; i >= 0; i--) {
                AbstractDungeon.actionManager.addToTop(captured.get(i));
            }
        }));
    }

    private AbstractGameAction doRedirect(AbstractGameAction action) {
        AbstractMonster ownerMon = (AbstractMonster) owner;
        action.source = ownerMon;
        if (action.target instanceof AbstractPlayer) {
            action.target = ownerMon;
        } else {
            action.target = Wiz.adp();
        }
        if (action instanceof ChannelAction) {
            AbstractOrb orb = ReflectionHacks.getPrivate(action, ChannelAction.class, "orbType");
            boolean auto = ReflectionHacks.getPrivate(action, ChannelAction.class, "autoEvoke");
            return new MonsterOrbHelper.MonsterChannelAction(ownerMon, orb, auto);
        }
        if (ActionAnalyzer.doesDamage(action)) {
            DamageInfo info = ActionAnalyzer.findDamage(action);
            AbstractGameAction.AttackEffect effect = action.attackEffect;
            if (info != null) {
                if (effect == AbstractGameAction.AttackEffect.LIGHTNING) {
                    DamageInfo newInfo = BindingHelper.makeInfo(lightning, owner, info.base, DamageInfo.DamageType.NORMAL);
                    return new DamageAction(Wiz.adp(), newInfo, AbstractGameAction.AttackEffect.NONE);
                } else {
                    DamageInfo newInfo = new DamageInfo(ownerMon, info.base, info.type);
                    newInfo.applyPowers(ownerMon, Wiz.adp());
                    return new DamageAction(Wiz.adp(), newInfo, effect);
                }
            } else {
                Object o = ActionCapturePatch.ActionFields.boundInstigator.get(action);
                Integer damage = null;
                DamageInfo.DamageType type = DamageInfo.DamageType.NORMAL;
                if (o instanceof AbstractCard) {
                    DamageInfo temp = new DamageInfo(ownerMon, ((AbstractCard) o).baseDamage, DamageInfo.DamageType.NORMAL);
                    temp.applyPowers(ownerMon, Wiz.adp());
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
                    return new DamageAction(Wiz.adp(), new DamageInfo(ownerMon, damage, type), effect);
                }
            }
        }
        return action;
    }
}
