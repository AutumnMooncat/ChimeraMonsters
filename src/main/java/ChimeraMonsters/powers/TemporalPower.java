package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.DamageInfoFields;
import ChimeraMonsters.powers.interfaces.DelayDamagePower;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.ColorUtil;
import ChimeraMonsters.vfx.ParticleEffect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class TemporalPower extends AbstractEasyPower implements RenderModifierPower, DelayDamagePower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(TemporalPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private float particleTimer;
    private float timePassed;

    public TemporalPower(AbstractCreature owner) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        timePassed += Gdx.graphics.getDeltaTime();
        Color origColor = sb.getColor();
        sb.setColor(new Color(0.85f, 0.85f, 0.85f, (float) (0.85f+Math.cos(timePassed*2f)*0.15f)));
        sb.draw(tex, tex.getRegionWidth(), tex.getRegionHeight(), new Affine2().setToShearing((float) (Math.sin(timePassed)/15f), 0f));
        sb.setColor(origColor);
    }

    @Override
    public void updateParticles() {
        particleTimer -= Gdx.graphics.getDeltaTime();
        if (particleTimer <= 0) {
            particleTimer = MathUtils.random(0.25f, 0.35f);
            float factor = Math.max(owner.hb.width, owner.hb.height)/75f;
            AbstractDungeon.effectsQueue.add(new ParticleEffect(ColorUtil.SKY_BLUE, owner.hb, factor, 1f));
        }
    }

    @Override
    public boolean shouldDelay(DamageInfo info) {
        if (DamageInfoFields.temporalDelay.get(info)) {
            return false;
        }
        flash();
        DamageInfoFields.temporalDelay.set(info, true);
        DamageInfoFields.lockedDamage.set(info, info.output);
        addToTop(new ApplyPowerAction(owner, owner, new NextTurnTakeDamagePower(owner, info, AbstractGameAction.AttackEffect.FIRE)));
        for (int i = 0; i < info.output; ++i) {
            AbstractDungeon.effectsQueue.add(new ParticleEffect(ColorUtil.SKY_BLUE, owner.hb, 0.5f, 1f + info.output));
        }
        CardCrawlGame.sound.play("ORB_DARK_CHANNEL", 0.1F); // ORB_DARK_EVOKE ORB_DARK_CHANNEL
        return true;
    }
}
