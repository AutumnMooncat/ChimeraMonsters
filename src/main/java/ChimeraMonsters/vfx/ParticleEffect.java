package ChimeraMonsters.vfx;

import ChimeraMonsters.util.CustomLighting;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class ParticleEffect extends AbstractGameEffect implements CustomLighting {
    protected float x;
    protected float y;
    protected float oX;
    protected float oY;
    protected float vX;
    protected float vY;
    protected final float dur_div2;
    protected final Hitbox hb;
    protected final TextureAtlas.AtlasRegion img;

    public ParticleEffect(Color c, float x, float y) {
        this(c, null);
        this.x = x;
        this.y = y;
    }

    public ParticleEffect(Color c, Hitbox hb) {
        this.hb = hb;
        this.img = ImageMaster.GLOW_SPARK_2;
        this.duration = MathUtils.random(1.8F, 2.0F);
        this.scale = MathUtils.random(1.0F, 1.2F) * Settings.scale;
        this.dur_div2 = this.duration / 2.0F;
        this.color = c;
        this.oX = MathUtils.random(-25.0F, 25.0F) * Settings.scale;
        this.oY = MathUtils.random(-25.0F, 25.0F) * Settings.scale;
        this.oX -= (float)this.img.packedWidth / 2.0F;
        this.oY -= (float)this.img.packedHeight / 2.0F;
        this.vX = MathUtils.random(-15.0F, 15.0F) * Settings.scale;
        this.vY = MathUtils.random(-17.0F, 17.0F) * Settings.scale;
        this.renderBehind = MathUtils.randomBoolean(0.2F + (this.scale - 0.5F));
        this.rotation = MathUtils.random(-8.0F, 8.0F);
    }

    public void update() {
        if (duration > dur_div2) {
            color.a = Interpolation.pow3In.apply(0.5F, 0.0F, (duration - dur_div2) / dur_div2);
        } else {
            color.a = Interpolation.pow3In.apply(0.0F, 0.5F, duration / dur_div2);
        }

        oX += vX * Gdx.graphics.getDeltaTime();
        oY += vY * Gdx.graphics.getDeltaTime();
        duration -= Gdx.graphics.getDeltaTime();
        if (duration < 0.0F) {
            isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(color);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        if (hb != null) {
            sb.draw(img, hb.cX + oX, hb.cY + oY, img.packedWidth / 2.0F, img.packedHeight / 2.0F, img.packedWidth, img.packedHeight, scale * MathUtils.random(0.8F, 1.2F), scale * MathUtils.random(0.8F, 1.2F), rotation);
        } else {
            sb.draw(img, x + oX, y + oY, img.packedWidth / 2.0F, img.packedHeight / 2.0F, img.packedWidth, img.packedHeight, scale * MathUtils.random(0.8F, 1.2F), scale * MathUtils.random(0.8F, 1.2F), rotation);
        }
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void dispose() {}

    @Override
    public float[] _lightsOutGetXYRI() {
        if (hb != null) {
            return new float[] {hb.cX + oX, hb.cY + oY, 150f, 0.05f};
        }
        return new float[] {x + oX, y + oY, 150f, 0.05f};
    }

    @Override
    public Color[] _lightsOutGetColor() {
        return new Color[] {color};
    }
}
