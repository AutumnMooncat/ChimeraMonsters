package ChimeraMonsters.vfx.hexaghost;

import ChimeraMonsters.util.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.vfx.BobEffect;

public class ScalableHexaBody {
    private static final int W = 512;
    private static Texture plasma1;
    private static Texture plasma2;
    private static Texture plasma3;
    private static Texture shadow;
    private static boolean initTex;
    public float targetRotationSpeed = 30.0F;
    private float rotationSpeed = 1.0F;
    public BobEffect effect = new BobEffect(0.75F);
    private AbstractCreature m;
    public float plasma1Angle = 0.0F;
    public float plasma2Angle = 0.0F;
    public float plasma3Angle = 0.0F;
    private static final float bodyOffsetY;
    private float scale;

    public ScalableHexaBody(AbstractCreature m, float scale) {
        this.m = m;// 35
        this.scale = scale;
        if (!initTex) {
            initTex = true;
            plasma1 = TextureLoader.getTexture("images/monsters/theBottom/boss/ghost/plasma1.png");// 36
            plasma2 = TextureLoader.getTexture("images/monsters/theBottom/boss/ghost/plasma2.png");// 37
            plasma3 = TextureLoader.getTexture("images/monsters/theBottom/boss/ghost/plasma3.png");// 38
            shadow = TextureLoader.getTexture("images/monsters/theBottom/boss/ghost/shadow.png");// 39
            //shadow = TextureLoader.getTexture("images/ui/intent/attack/attack_intent_4.png");
        }
    }// 40

    public void update() {
        this.effect.update();// 43
        this.plasma1Angle += this.rotationSpeed * Gdx.graphics.getDeltaTime();// 44
        this.plasma2Angle += this.rotationSpeed / 2.0F * Gdx.graphics.getDeltaTime();// 45
        this.plasma3Angle += this.rotationSpeed / 3.0F * Gdx.graphics.getDeltaTime();// 46
        this.rotationSpeed = MathHelper.fadeLerpSnap(this.rotationSpeed, this.targetRotationSpeed);// 48
        this.effect.speed = this.rotationSpeed * Gdx.graphics.getDeltaTime();// 49
    }// 50

    public void render(SpriteBatch sb) {
        //sb.setColor(this.m.tint.color);// 53
        sb.draw(plasma3, this.m.drawX - 256.0F + this.m.animX + 12.0F * Settings.scale * scale, this.m.drawY + this.m.animY + this.effect.y * 2.0F - 256.0F + bodyOffsetY * scale, 256.0F, 256.0F, 512.0F, 512.0F, Settings.scale * 0.95F * scale, Settings.scale * 0.95F * scale, this.plasma3Angle, 0, 0, 512, 512, false, false);// 54
        sb.draw(plasma2, this.m.drawX - 256.0F + this.m.animX + 6.0F * Settings.scale * scale, this.m.drawY + this.m.animY + this.effect.y - 256.0F + bodyOffsetY * scale, 256.0F, 256.0F, 512.0F, 512.0F, Settings.scale * scale, Settings.scale * scale, this.plasma2Angle, 0, 0, 512, 512, false, false);// 71
        sb.draw(plasma1, this.m.drawX - 256.0F + this.m.animX, this.m.drawY + this.m.animY + this.effect.y * 0.5F - 256.0F + bodyOffsetY * scale, 256.0F, 256.0F, 512.0F, 512.0F, Settings.scale * scale, Settings.scale * scale, this.plasma1Angle, 0, 0, 512, 512, false, false);// 88
        sb.draw(shadow, this.m.drawX - 256.0F + this.m.animX + 12.0F * Settings.scale * scale, this.m.drawY + this.m.animY + this.effect.y / 4.0F - 15.0F * Settings.scale * scale - 256.0F + bodyOffsetY * scale, 256.0F, 256.0F, 512.0F, 512.0F, Settings.scale * scale, Settings.scale * scale, 0.0F, 0, 0, 512, 512, false, false);// 106
    }// 123

    static {
        bodyOffsetY = 256.0F * Settings.scale;// 32
    }
}
