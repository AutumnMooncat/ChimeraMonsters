package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.ColorUtil;
import ChimeraMonsters.vfx.ParticleEffect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class AbyssalPower extends AbstractEasyPower implements RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(AbyssalPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private float particleTimer;
    private boolean primed;

    public AbyssalPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (action.target == owner && !primed) {
            flash();
            startFlashing();
            primed = true;
        }
    }
    @Override
    public void duringTurn() {
        if (primed) {
            stopFlashing();
            primed = false;
            addToBot(new MakeTempCardInDiscardAction(new VoidCard(), amount));
        }
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        Color origColor = sb.getColor();
        sb.setColor(new Color(0.85f, 0.75f, 0.85f, 1f));
        float sx = owner.hb.width > owner.hb.height ? 1.1f : 0.9f;
        float sy = owner.hb.width > owner.hb.height ? 0.9f : 1.1f;
        render(sb, tex, 0, 0, sx, sy, 0f);
        sb.setColor(origColor);
    }

    @Override
    public void updateParticles() {
        if (primed) {
            particleTimer -= Gdx.graphics.getDeltaTime();
            if (particleTimer <= 0) {
                particleTimer = MathUtils.random(0.25f, 0.3f);
                float factor = Math.max(owner.hb.width, owner.hb.height)/40f;
                for (int i = 0; i < 4; ++i) {
                    AbstractDungeon.effectsQueue.add(new ParticleEffect(ColorUtil.AMETHYST, owner.hb, factor, 1.5f));
                }
            }
        }
    }
}
