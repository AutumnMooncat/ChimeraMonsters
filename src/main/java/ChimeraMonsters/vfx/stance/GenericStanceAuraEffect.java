package ChimeraMonsters.vfx.stance;

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

public class GenericStanceAuraEffect extends AbstractGameEffect implements CustomLighting {
    private static boolean switcher = true;
    private float x;
    private float y;
    private float vY;
    private final TextureAtlas.AtlasRegion img;

    public static Color wrathColor() {
        return new Color(MathUtils.random(0.6F, 0.7F), MathUtils.random(0.0F, 0.1F), MathUtils.random(0.1F, 0.2F), 0.0F);
    }

    public static Color calmColor() {
        return new Color(MathUtils.random(0.5F, 0.55F), MathUtils.random(0.6F, 0.7F), 1.0F, 0.0F);
    }

    public static Color divinityColor() {
        return new Color(MathUtils.random(0.6F, 0.7F), MathUtils.random(0.0F, 0.1F), MathUtils.random(0.6F, 0.7F), 0.0F);
    }

    public GenericStanceAuraEffect(Color c, Hitbox hb) {
        this.img = ImageMaster.EXHAUST_L;
        this.duration = 2.0F;
        this.scale = MathUtils.random(2.7F, 2.5F) * Settings.scale;
        this.color = c.cpy();
        this.x = hb.cX + MathUtils.random(-hb.width / 16.0F, hb.width / 16.0F);
        this.y = hb.cY + MathUtils.random(-hb.height / 16.0F, hb.height / 12.0F);
        this.x -= (float)this.img.packedWidth / 2.0F;
        this.y -= (float)this.img.packedHeight / 2.0F;
        switcher = !switcher;
        this.rotation = MathUtils.random(360.0F);
        if (switcher) {
            this.renderBehind = true;
            this.vY = MathUtils.random(0.0F, 40.0F);
        } else {
            this.renderBehind = false;
            this.vY = MathUtils.random(0.0F, -40.0F);
        }
    }

    public void update() {
        if (duration > 1.0F) {
            color.a = Interpolation.fade.apply(0.3F, 0.0F, duration - 1.0F);
        } else {
            color.a = Interpolation.fade.apply(0.0F, 0.3F, duration);
        }

        rotation += Gdx.graphics.getDeltaTime() * vY;
        duration -= Gdx.graphics.getDeltaTime();
        if (duration < 0.0F) {
            isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(color);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        sb.draw(img, x, y, img.packedWidth / 2.0F, img.packedHeight / 2.0F, img.packedWidth, img.packedHeight, scale, scale, rotation);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void dispose() {}

    @Override
    public float[] _lightsOutGetXYRI() {
        return new float[] {x, y, 300f, 0.1f};
    }

    @Override
    public Color[] _lightsOutGetColor() {
        return new Color[] {color};
    }
}
