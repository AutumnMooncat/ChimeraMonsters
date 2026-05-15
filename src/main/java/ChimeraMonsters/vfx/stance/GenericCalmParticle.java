package ChimeraMonsters.vfx.stance;

import ChimeraMonsters.util.CustomLighting;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class GenericCalmParticle extends AbstractGameEffect implements CustomLighting {
    private float x;
    private float y;
    private float vX;
    private float vY;
    private float dur_div2;
    private float dvy;
    private float dvx;

    public static Color originalcolor() {
        return new Color(MathUtils.random(0.2F, 0.3F), MathUtils.random(0.65F, 0.8F), 1.0F, 0.0F);
    }

    public GenericCalmParticle(Color c, float x, float y) {
        this.duration = MathUtils.random(0.6F, 1.0F);// 18
        this.scale = MathUtils.random(0.6F, 1.2F) * Settings.scale;// 19
        this.dur_div2 = this.duration / 2.0F;// 20
        this.color = c.cpy();
        this.vX = MathUtils.random(-300.0F, -50.0F) * Settings.scale;// 22
        this.vY = MathUtils.random(-200.0F, -100.0F) * Settings.scale;// 23
        this.x = x + MathUtils.random(100.0F, 160.0F) * Settings.scale - 32.0F;// 24
        this.y = y + MathUtils.random(-50.0F, 220.0F) * Settings.scale - 32.0F;// 25
        this.renderBehind = MathUtils.randomBoolean(0.2F + (this.scale - 0.5F));// 26
        this.dvx = 400.0F * Settings.scale * this.scale;// 27
        this.dvy = 100.0F * Settings.scale;// 28
    }// 29

    public void update() {
        this.x += this.vX * Gdx.graphics.getDeltaTime();// 33
        this.y += this.vY * Gdx.graphics.getDeltaTime();// 34
        this.vY += Gdx.graphics.getDeltaTime() * this.dvy;// 35
        this.vX -= Gdx.graphics.getDeltaTime() * this.dvx;// 36
        this.rotation = -((180F / (float)Math.PI) * MathUtils.atan2(this.vX, this.vY)) - 0.0F;// 37
        if (this.duration > this.dur_div2) {// 39
            this.color.a = Interpolation.fade.apply(1.0F, 0.0F, (this.duration - this.dur_div2) / this.dur_div2);// 40
        } else {
            this.color.a = Interpolation.fade.apply(0.0F, 1.0F, this.duration / this.dur_div2);// 42
        }

        this.duration -= Gdx.graphics.getDeltaTime();// 45
        if (this.duration < 0.0F) {// 46
            this.isDone = true;// 47
        }

    }// 49

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);// 53
        sb.setBlendFunction(770, 1);// 54
        sb.draw(ImageMaster.FROST_ACTIVATE_VFX_1, this.x, this.y, 32.0F, 32.0F, 25.0F, 128.0F, this.scale, this.scale + (this.dur_div2 * 0.4F - this.duration) * Settings.scale, this.rotation, 0, 0, 64, 64, false, false);// 55
        sb.setBlendFunction(770, 771);// 72
    }// 73

    public void dispose() {
    }// 77

    @Override
    public float[] _lightsOutGetXYRI() {
        return new float[] {x, y, 50f, 0.1f};
    }

    @Override
    public Color[] _lightsOutGetColor() {
        return new Color[] {color};
    }
}
