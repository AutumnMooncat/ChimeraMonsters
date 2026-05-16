package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.patches.MoveManipulationPatches;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.vfx.hexaghost.ScalableHexaBody;
import ChimeraMonsters.vfx.hexaghost.ScalableHexaOrb;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.vfx.combat.GhostIgniteEffect;
import com.megacrit.cardcrawl.vfx.combat.ScreenOnFireEffect;

import java.util.ArrayList;

public class HexaghostPower extends AbstractInternalLogicPower implements IntentInterceptingPower, RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(HexaghostPower.class.getSimpleName());
    private EnemyMoveInfo lastMove;
    private boolean activated;
    private boolean divided;
    private final ScalableHexaBody body;
    private final ArrayList<ScalableHexaOrb> orbs = new ArrayList<>();
    private boolean dontOrbThisTurn;
    private final DamageInfo divider;
    private final DamageInfo inferno;
    private final float scale;

    public HexaghostPower(AbstractCreature owner, int dmg) {
        super(POWER_ID, owner, -1);
        priority = -50;
        scale = Math.max(owner.hb_w / 400f, owner.hb_h / 400f);
        body = new ScalableHexaBody(owner, scale);
        orbs.add(new ScalableHexaOrb(-90.0F, 380.0F, this.orbs.size(), scale));// 115
        orbs.add(new ScalableHexaOrb(90.0F, 380.0F, this.orbs.size(), scale));// 116
        orbs.add(new ScalableHexaOrb(160.0F, 250.0F, this.orbs.size(), scale));// 117
        orbs.add(new ScalableHexaOrb(90.0F, 120.0F, this.orbs.size(), scale));// 118
        orbs.add(new ScalableHexaOrb(-90.0F, 120.0F, this.orbs.size(), scale));// 119
        orbs.add(new ScalableHexaOrb(-160.0F, 250.0F, this.orbs.size(), scale));// 120
        divider = new DamageInfo(owner, -1);
        inferno = new DamageInfo(owner, dmg);
    }

    @Override
    public void duringTurn() {
        if (!activated) {
            return;
        }
        if (dontOrbThisTurn) {
            dontOrbThisTurn = false;
            return;
        }

        addToBot(new DoAction(() -> {
            for (ScalableHexaOrb orb : this.orbs) {
                if (!orb.activated) {
                    orb.activate(owner.drawX + owner.animX, owner.drawY + owner.animY);
                    break;
                }
            }

            if (owner instanceof AbstractMonster && orbs.stream().allMatch(orb -> orb.activated)) {
                MoveManipulationPatches.removeAndResetInterceptor((AbstractMonster) owner);
                MoveManipulationPatches.applyInterceptor((AbstractMonster) owner, HexaghostPower.this, getMove(owner));
            }
        }));
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        body.update();
        for (ScalableHexaOrb orb : orbs) {
            orb.update(owner.drawX + owner.animX, owner.drawY + owner.animY);
        }
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        body.render(sb);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        render(sb, tex);
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        if (!activated) {
            return 1;
        }
        if (dontOrbThisTurn) {
            return 0;
        }
        return orbs.stream().allMatch(orb -> orb.activated) ? 1 : 0;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        lastMove = replacedMove;
        if (!activated) {
            overrideMove(owner, new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.UNKNOWN, -1, 0, false));
        }  else {
            overrideMove(owner, new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.ATTACK_DEBUFF, inferno.base, 6, true));
        }
    }

    @Override
    public boolean performIntercept() {
        if (!activated) {
            activated = true;
            dontOrbThisTurn = true;
            addToBot(new DoAction(() -> {
                for(ScalableHexaOrb orb : orbs) {// 270
                    orb.activate(owner.drawX + owner.animX, owner.drawY + owner.animY);
                }
                body.targetRotationSpeed = 120f;
            }));
            return false;
        }
        if (!divided) {
            divided = true;
            dontOrbThisTurn = true;
            divider.applyPowers(owner, AbstractDungeon.player);

            for (int i = 0; i < 6; ++i) {// 139
                addToBot(new VFXAction(owner, new GhostIgniteEffect(AbstractDungeon.player.hb.cX + MathUtils.random(-120.0F, 120.0F) * Settings.scale, AbstractDungeon.player.hb.cY + MathUtils.random(-120.0F, 120.0F) * Settings.scale), 0.05F));// 140 144 145
                if (MathUtils.randomBoolean()) {// 147
                    addToBot(new SFXAction("GHOST_ORB_IGNITE_1", 0.3F));// 148
                } else {
                    addToBot(new SFXAction("GHOST_ORB_IGNITE_2", 0.3F));// 150
                }
                addToBot(new DamageAction(AbstractDungeon.player, divider, AbstractGameAction.AttackEffect.BLUNT_HEAVY, true));// 152 153
            }

            addToBot(new DoAction(() -> {
                for (ScalableHexaOrb orb : this.orbs) {
                    orb.deactivate();
                }
                CardCrawlGame.sound.play("CARD_EXHAUST", 0.2F);// 297
                CardCrawlGame.sound.play("CARD_EXHAUST", 0.2F);// 298
            }));
        } else {
            dontOrbThisTurn = true;
            inferno.applyPowers(owner, AbstractDungeon.player);

            addToBot(new VFXAction(owner, new ScreenOnFireEffect(), 1.0F));// 204
            for (int i = 0; i < 6; ++i) {// 205
                addToBot(new DamageAction(AbstractDungeon.player, inferno, AbstractGameAction.AttackEffect.FIRE));// 206 207
            }

            addToBot(new MakeTempCardInDiscardAction(new Burn(), 2));
            /*AbstractDungeon.actionManager.addToBottom(new BurnIncreaseAction());// 209
            if (!this.burnUpgraded) {// 210
                this.burnUpgraded = true;// 211
            }*/

            addToBot(new DoAction(() -> {
                for (ScalableHexaOrb orb : this.orbs) {
                    orb.deactivate();
                }
                CardCrawlGame.sound.play("CARD_EXHAUST", 0.2F);// 297
                CardCrawlGame.sound.play("CARD_EXHAUST", 0.2F);// 298
            }));
        }
        setMove(owner, lastMove);
        return true;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        if (!divided) {
            int div = 24;
            if (owner instanceof AbstractMonster && ((AbstractMonster) owner).type == AbstractMonster.EnemyType.ELITE) {
                div = 18;
            }
            divider.base = AbstractDungeon.player.currentHealth/div + 1;
            divider.applyPowers(owner, AbstractDungeon.player);
            overrideMove(owner, new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.ATTACK, divider.base, 6, true));
            return true;
        }
        return false;
    }
}
