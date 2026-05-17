package ChimeraMonsters.vfx;

import ChimeraMonsters.util.Wiz;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class VFXContainer {
    public static AbstractGameEffect hitBounce(Texture tex, float scale, Hitbox target) {
        return new VfxBuilder(tex, target.cX, target.cY,1.5f)
                .setScale(scale)
                .gravity(50f)
                .velocity(MathUtils.random(45f, 135f), MathUtils.random(600f, 800f))
                .rotate(MathUtils.random(50f, 200f) * (MathUtils.randomBoolean() ? -1 : 1))
                .build();
    }

    public static AbstractGameEffect hitBounce(Texture tex, float scale, float targetX, float targetY) {
        return new VfxBuilder(tex, targetX, targetY,1.5f)
                .setScale(scale)
                .gravity(50f)
                .velocity(MathUtils.random(45f, 135f), MathUtils.random(600f, 800f))
                .rotate(MathUtils.random(50f, 200f) * (MathUtils.randomBoolean() ? -1 : 1))
                .build();
    }

    public static AbstractGameEffect throwEffect(Texture tex, float scale, Hitbox target, Color color, boolean bounceOff, boolean sfx) {
        VfxBuilder builder = new VfxBuilder(tex, Wiz.adp().hb.cX, Wiz.adp().hb.cY, 0.25f)
                .moveX(Wiz.adp().hb.cX, target.cX, VfxBuilder.Interpolations.POW2OUT)
                .moveY(Wiz.adp().hb.cY, target.cY, VfxBuilder.Interpolations.POW2OUT)
                .rotate(MathUtils.random(100f, 300f) * (MathUtils.randomBoolean() ? -1 : 1))
                .setScale(scale)
                .emitEvery((x,y) -> new ParticleEffect(color.cpy(), x, y), 0.01f);
        if (sfx) {
            builder = builder.playSoundAt(0.0f, "ATTACK_WHIFF_2");
        }
        if (bounceOff) {
            builder = builder.triggerVfxAt(0.25f, 1, (x,y) -> hitBounce(tex, scale, target));
        }
        return builder.build();
    }

    public static AbstractGameEffect throwEffect(Texture tex, float scale, Hitbox source, Hitbox target, Color color, boolean bounceOff, boolean sfx) {
        VfxBuilder builder = new VfxBuilder(tex, source.cX, source.cY, 0.25f)
                .moveX(source.cX, target.cX, VfxBuilder.Interpolations.POW2OUT)
                .moveY(source.cY, target.cY, VfxBuilder.Interpolations.POW2OUT)
                .rotate(MathUtils.random(100f, 300f) * (MathUtils.randomBoolean() ? -1 : 1))
                .setScale(scale)
                .emitEvery((x,y) -> new ParticleEffect(color.cpy(), x, y), 0.01f);
        if (sfx) {
            builder = builder.playSoundAt(0.0f, "ATTACK_WHIFF_2");
        }
        if (bounceOff) {
            builder = builder.triggerVfxAt(0.25f, 1, (x,y) -> hitBounce(tex, scale, target));
        }
        return builder.build();
    }

    public static AbstractGameEffect throwEffect(Texture tex, float scale, float sourceX, float sourceY, Hitbox target, Color color, boolean bounceOff, boolean sfx) {
        VfxBuilder builder = new VfxBuilder(tex, sourceX, sourceY, 0.25f)
                .moveX(sourceX, target.cX, VfxBuilder.Interpolations.POW2OUT)
                .moveY(sourceY, target.cY, VfxBuilder.Interpolations.POW2OUT)
                .rotate(MathUtils.random(100f, 300f) * (MathUtils.randomBoolean() ? -1 : 1))
                .setScale(scale)
                .emitEvery((x,y) -> new ParticleEffect(color.cpy(), x, y), 0.01f);
        if (sfx) {
            builder = builder.playSoundAt(0.0f, "ATTACK_WHIFF_2");
        }
        if (bounceOff) {
            builder = builder.triggerVfxAt(0.25f, 1, (x,y) -> hitBounce(tex, scale, target));
        }
        return builder.build();
    }

    public static AbstractGameEffect throwEffect(Texture tex, float scale, float sourceX, float sourceY, float targetX, float targetY, Color color, boolean bounceOff, boolean sfx) {
        VfxBuilder builder = new VfxBuilder(tex, sourceX, sourceY, 0.25f)
                .moveX(sourceX, targetX, VfxBuilder.Interpolations.POW2OUT)
                .moveY(sourceY, targetY, VfxBuilder.Interpolations.POW2OUT)
                .rotate(MathUtils.random(100f, 300f) * (MathUtils.randomBoolean() ? -1 : 1))
                .setScale(scale)
                .emitEvery((x,y) -> new ParticleEffect(color.cpy(), x, y), 0.01f);
        if (sfx) {
            builder = builder.playSoundAt(0.0f, "ATTACK_WHIFF_2");
        }
        if (bounceOff) {
            builder = builder.triggerVfxAt(0.25f, 1, (x,y) -> hitBounce(tex, scale, targetX, targetY));
        }
        return builder.build();
    }
}