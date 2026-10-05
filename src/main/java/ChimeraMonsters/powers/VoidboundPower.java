package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.ColorUtil;
import ChimeraMonsters.vfx.ParticleEffect;
import ChimeraMonsters.vfx.stance.GenericStanceAuraEffect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class VoidboundPower extends AbstractEasyPower implements RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(VoidboundPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private float particleTimer;
    private float timer, baseTimer;
    private boolean primed;

    public VoidboundPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public void onDeath() {
        flash();
        primed = true;
        baseTimer = timer = 2f;
        CardCrawlGame.sound.play("ORB_DARK_EVOKE", 0.1F); // ORB_DARK_EVOKE ORB_DARK_CHANNEL
        addToBot(new MakeTempCardInDrawPileAction(new VoidCard(), amount, false, true, false));
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        Color origColor = sb.getColor();
        if (!primed) {
            sb.setColor(new Color(1f, 0.8f, 1f, 1f));
            render(sb, tex);
            sb.setColor(origColor);
            return;
        }

        if (timer > 0) {
            if (owner instanceof AbstractMonster) {
                ((AbstractMonster) owner).deathTimer += Gdx.graphics.getDeltaTime();
                ((AbstractMonster) owner).tintFadeOutCalled = false;
                owner.tint.color.a = 1f;
            }
            timer = Math.max(0f, timer - Gdx.graphics.getDeltaTime());
        }
        float dt = timer/baseTimer;
        sb.setColor(new Color(dt, 0.8f*dt, dt, 1f));
        render(sb, tex, (float) (Math.sin(timer)*15f), (float) (Math.cos(timer)*15f), dt, dt, 360f*(baseTimer*baseTimer)/(timer*timer));
        sb.setColor(origColor);
    }

    @Override
    public void updateParticles() {
        particleTimer -= Gdx.graphics.getDeltaTime();
        if (particleTimer <= 0) {
            float factor = Math.max(owner.hb.width, owner.hb.height)/75f;
            if (primed) {
                particleTimer = MathUtils.random(0.15f, 0.2f);
                AbstractDungeon.effectsQueue.add(new GenericStanceAuraEffect(ColorUtil.AMETHYST, owner.hb));
                for (int i = 0; i < 6; ++i) {
                    AbstractDungeon.effectsQueue.add(new ParticleEffect(ColorUtil.AMETHYST, owner.hb, factor * timer/2f, 4f/timer));
                }
            } else {
                particleTimer = MathUtils.random(0.25f, 0.3f);
                for (int i = 0; i < 4; ++i) {
                    AbstractDungeon.effectsQueue.add(new ParticleEffect(ColorUtil.AMETHYST, owner.hb, factor, 1.5f));
                }
            }
        }
    }
}
