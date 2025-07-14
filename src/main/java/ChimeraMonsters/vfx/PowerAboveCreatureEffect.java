package ChimeraMonsters.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class PowerAboveCreatureEffect extends AbstractGameEffect {
    private static final float TEXT_DURATION = 1.5F;
    private static final float STARTING_OFFSET_Y = 0.0F * Settings.scale;
    private static final float TARGET_OFFSET_Y = 60.0F * Settings.scale;
    private static final float LERP_RATE = 5.0F;
    private static final int W = 128;
    private final float x;
    private float y;
    private float offsetY;
    private final AbstractPower power;
    private final Color outlineColor = new Color(0.0F, 0.0F, 0.0F, 0.0F);
    private final Color shineColor = new Color(1.0F, 1.0F, 1.0F, 0.0F);

    public PowerAboveCreatureEffect(float x, float y, AbstractPower power) {
        this.duration = 1.5F;
        this.startingDuration = 1.5F;
        this.power = power;
        this.x = x;
        this.y = y;
        this.color = Color.WHITE.cpy();
        this.offsetY = STARTING_OFFSET_Y;
        this.scale = Settings.scale;
    }

    public void update() {
        if (duration > 1.0F) {
            color.a = Interpolation.exp5In.apply(1.0F, 0.0F, (duration - 1.0F) * 2.0F);
        }

        super.update();
        if (AbstractDungeon.player.chosenClass == AbstractPlayer.PlayerClass.DEFECT) {
            offsetY = MathUtils.lerp(offsetY, TARGET_OFFSET_Y + 80.0F * Settings.scale, Gdx.graphics.getDeltaTime() * 5.0F);
        } else {
            offsetY = MathUtils.lerp(offsetY, TARGET_OFFSET_Y, Gdx.graphics.getDeltaTime() * 5.0F);
        }

        y += Gdx.graphics.getDeltaTime() * 12.0F * Settings.scale;
    }

    public void render(SpriteBatch sb) {
        outlineColor.a = color.a / 2.0F;
        sb.setColor(color);
        sb.draw(power.region128, x - power.region128.packedWidth/2f, y - power.region128.packedHeight/2f, power.region128.packedWidth/2f, power.region128.packedHeight/2f, power.region128.packedWidth, power.region128.packedHeight, scale * (2.5F - duration), scale * (2.5F - duration), rotation);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        shineColor.a = color.a / 4.0F;
        sb.setColor(shineColor);
        sb.draw(power.region128, x - power.region128.packedWidth/2f, y - power.region128.packedHeight/2f, power.region128.packedWidth/2f, power.region128.packedHeight/2f, power.region128.packedWidth, power.region128.packedHeight, scale * (2.7F - duration), scale * (2.7F - duration), rotation);
        sb.draw(power.region128, x - power.region128.packedWidth/2f, y - power.region128.packedHeight/2f, power.region128.packedWidth/2f, power.region128.packedHeight/2f, power.region128.packedWidth, power.region128.packedHeight, scale * (3.0F - duration), scale * (3.0F - duration), rotation);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void dispose() {}
}
