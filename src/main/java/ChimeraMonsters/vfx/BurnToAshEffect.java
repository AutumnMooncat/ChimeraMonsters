package ChimeraMonsters.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class BurnToAshEffect extends AbstractGameEffect {
    private final float x;
    private final float y;

    public BurnToAshEffect(float x, float y) {
        this.x = x;
        this.y = y;
        this.duration = 0.2F;
    }

    public void update() {
        if (duration == 0.2F) {
            for (int i = 0; i < 90; ++i) {
                AbstractDungeon.effectsQueue.add(new SmokeBlurEffect(x, y));
            }
        }

        duration -= Gdx.graphics.getDeltaTime();
        if (duration < 0.0F) {
            CardCrawlGame.sound.play("APPEAR");
            isDone = true;
        }
    }

    public void render(SpriteBatch sb) {}

    public void dispose() {}

    public static class SmokeBlurEffect extends AbstractGameEffect {
        private float x;
        private float y;
        private final float pV;
        private final float aV;
        private final float startDur;
        private final float targetScale;
        private final TextureAtlas.AtlasRegion img;

        public SmokeBlurEffect(float x, float y) {
            this.color = new Color(0.0F, 0.0F, 0.0F, 1.0F);
            float v = MathUtils.random(0.5F, 0.6F);
            this.color.r = v + MathUtils.random(0.0F, 0.1F);
            this.color.g = v;
            this.color.b = v;
            if (MathUtils.randomBoolean()) {
                this.img = ImageMaster.EXHAUST_L;
                this.duration = MathUtils.random(2.0F, 2.5F);
                this.targetScale = MathUtils.random(0.4F, 1.1F);
            } else {
                this.img = ImageMaster.EXHAUST_S;
                this.duration = MathUtils.random(2.0F, 2.5F);
                this.targetScale = MathUtils.random(0.4F, 0.6F);
            }

            this.startDur = this.duration;
            this.x = x + MathUtils.random(-180.0F * Settings.scale, 150.0F * Settings.scale) - img.packedWidth / 2.0F;
            this.y = y + MathUtils.random(-240.0F * Settings.scale, 150.0F * Settings.scale) - img.packedHeight / 2.0F;
            this.scale = 0.01F;
            this.rotation = MathUtils.random(360.0F);
            this.aV = MathUtils.random(-250.0F, 250.0F);
            this.pV = MathUtils.random(Settings.scale, 5.0F * Settings.scale);
        }

        public void update() {
            duration -= Gdx.graphics.getDeltaTime();
            if (duration < 0.0F) {
                isDone = true;
            }

            x += MathUtils.random(-2.0F * Settings.scale, 2.0F * Settings.scale);
            x += pV;
            y += MathUtils.random(-2.0F * Settings.scale, 2.0F * Settings.scale);
            y += pV;
            rotation += aV * Gdx.graphics.getDeltaTime();
            scale = Interpolation.exp10Out.apply(0.01F, targetScale, 1.0F - duration / startDur);
            if (duration < 0.33F) {
                color.a = duration * 3.0F;
            }
        }

        public void render(SpriteBatch sb) {
            sb.setColor(color);
            sb.draw(img, x, y, img.packedWidth / 2.0F, img.packedHeight / 2.0F, img.packedWidth, img.packedHeight, scale, scale, rotation);
        }

        public void dispose() {}
    }
}
