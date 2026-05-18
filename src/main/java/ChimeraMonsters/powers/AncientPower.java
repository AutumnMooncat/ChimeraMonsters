package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.ApplyPowerActionWithFollowup;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.ImpactSparkEffect;

public class AncientPower extends AbstractEasyPower implements RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(AncientPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private float particleTimer;
    private boolean primed;

    public AncientPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null && info.owner != owner) {
            addToTop(new ApplyPowerActionWithFollowup(
                    new ApplyPowerAction(owner, owner, new StrengthPower(owner, -amount)),
                    new ApplyPowerAction(owner, owner, new GainStrengthPower(owner, amount))
            ));
        }
        return damageAmount;
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        Color origColor = sb.getColor();
        sb.setColor(new Color(1f, 1f, 0.75f, 1f));
        render(sb, tex);
        sb.setColor(origColor);
    }

    @Override
    public float animationRate() {
        float s = 0.5f + MathUtils.cosDeg((float)(System.currentTimeMillis() / 10L % 360L))/2f;
        primed = s < 0.05f;
        return s;
    }

    @Override
    public void updateParticles() {
        if (primed) {
            particleTimer -= Gdx.graphics.getDeltaTime();
            if (particleTimer <= 0) {
                particleTimer = MathUtils.random(0.15f, 0.35f);
                for(int i = 0; i < 4; ++i) {// 21
                    AbstractDungeon.effectsQueue.add(new ImpactSparkEffect(owner.hb.cX, owner.hb.cY));
                }
            }
        }
    }
}
