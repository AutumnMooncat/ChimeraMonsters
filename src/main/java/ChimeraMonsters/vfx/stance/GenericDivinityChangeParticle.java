package ChimeraMonsters.vfx.stance;

import ChimeraMonsters.util.CustomLighting;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class GenericDivinityChangeParticle extends AbstractGameEffect implements CustomLighting {
    private final TextureAtlas.AtlasRegion img;
    private float oX;
    private float oY;
    private float x;
    private float y;
    private float aV;
    private float distOffset;
    private float scaleOffset;

    public GenericDivinityChangeParticle(Color color, float x, float y) {
        this.img = ImageMaster.STRIKE_LINE;// 15
        this.startingDuration = 0.5F;// 19
        this.duration = this.startingDuration;// 20
        this.color = color.cpy();// 21
        this.rotation = MathUtils.random(360.0F);// 22
        this.oX = x - (float)this.img.packedWidth / 2.0F + MathUtils.random(-10.0F, 10.0F) * Settings.scale;// 23
        this.oY = y - (float)this.img.packedHeight / 2.0F + MathUtils.random(-10.0F, 10.0F) * Settings.scale;// 24
        this.distOffset = MathUtils.random(800.0F, 1200.0F);// 25
        this.renderBehind = true;// 26
        this.aV = MathUtils.random(50.0F, 80.0F);// 27
        this.scaleOffset = MathUtils.random(4.0F, 5.0F);// 28
        this.aV = MathUtils.random(0.4F);// 30
    }

    public void update() {
        if (this.aV > 0.0F) {// 34
            this.aV -= Gdx.graphics.getDeltaTime();// 35
        } else {
            this.duration -= Gdx.graphics.getDeltaTime();// 39
            if (this.duration < 0.0F) {// 40
                this.isDone = true;// 41
            } else {
                this.x = this.oX + MathUtils.cosDeg(this.rotation) * this.distOffset * Interpolation.pow2In.apply(0.02F, 0.95F, this.duration * 2.0F) * Settings.scale;// 45
                this.y = this.oY + MathUtils.sinDeg(this.rotation) * this.distOffset * Interpolation.pow3In.apply(0.02F, 0.95F, this.duration * 2.0F) * Settings.scale;// 47
                this.duration -= Gdx.graphics.getDeltaTime();// 50
                this.scale = this.scaleOffset * (this.duration + 0.1F) * Settings.scale;// 51
                this.color.a = Interpolation.pow3In.apply(0.0F, 1.0F, this.duration * 2.0F);// 52
            }
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);// 57
        sb.setBlendFunction(770, 1);// 58
        sb.draw(this.img, this.x, this.y, (float)this.img.packedWidth / 2.0F, (float)this.img.packedHeight / 2.0F, (float)this.img.packedWidth, (float)this.img.packedHeight, this.scale, this.scale, this.rotation);// 59
        sb.setBlendFunction(770, 771);// 81
    }

    public void dispose() {}

    @Override
    public float[] _lightsOutGetXYRI() {
        return new float[] {x, y, 250f, 0.2f};
    }

    @Override
    public Color[] _lightsOutGetColor() {
        return new Color[] {color};
    }
}
