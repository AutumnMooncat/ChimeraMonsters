package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.CreatureRenderPatches;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.ColorUtil;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class VisualPermutationPower extends AbstractInternalLogicPower implements RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(VisualPermutationPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public final float scaleX, scaleY, anim;
    public final Color color;

    public VisualPermutationPower(AbstractCreature owner) {
        super(POWER_ID, owner, -1);
        scaleX = MathUtils.random(0.92f, 1.08f);
        scaleY = MathUtils.random(0.92f, 1.08f);
        anim = MathUtils.random(0.92f, 1.08f);
        color = ColorUtil.WHITE.diffuse(0.15f);
        owner.hb_w *= scaleX;
        owner.hb_h *= scaleY;
        owner.hb.width *= scaleX;
        owner.hb.height *= scaleY;
        owner.healthBarUpdatedEvent();
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        float factor = scaleY >= 1f ? scaleY - 1f : 1f - scaleY;
        Color origColor = sb.getColor();
        sb.setColor(color);
        render(sb, tex, 0f, CreatureRenderPatches.currentTransform()[4]*factor, scaleX, scaleY);
        sb.setColor(origColor);
    }

    @Override
    public float animationRate() {
        return anim;
    }
}
