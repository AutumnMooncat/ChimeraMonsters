package ChimeraMonsters.patches;

import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.ImageHelper;
import ChimeraMonsters.util.matchers.SuperFieldAccessMatcher;
import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;

public class CreatureRenderPatches {
    private static final FrameBuffer frontBuffer = ImageHelper.createBuffer();
    private static final FrameBuffer backBuffer = ImageHelper.createBuffer();
    private static final FrameBuffer shaderBuffer = ImageHelper.createBuffer();
    private static FrameBuffer activeBuffer = frontBuffer;
    private static boolean capturing = false;
    private static final float[] transform = new float[4];

    private static void beginCapture(AbstractCreature __instance, SpriteBatch sb) {
        capturing = false;
        if (__instance.powers.stream().anyMatch(p -> p instanceof RenderModifierPower)) {
            capturing = true;
            prepareTransform(__instance);
            startBuffer(sb);
        }
    }

    private static void endCapture(AbstractCreature __instance, SpriteBatch sb) {
        if (capturing) {
            endBuffer(sb);
            sb.end();
            for (AbstractPower power : __instance.powers) {
                if (power instanceof RenderModifierPower) {
                    blit((RenderModifierPower) power);
                }
            }
            sb.begin();
            draw(sb, ImageHelper.getBufferTexture(activeBuffer));
            undoTransform(__instance);
            capturing = false;
        }
    }

    private static void blit(RenderModifierPower renderer) {
        TextureRegion tex = ImageHelper.getBufferTexture(activeBuffer);
        swapBuffers();
        SpriteBatch temp = new SpriteBatch();
        ImageHelper.beginBuffer(activeBuffer);
        temp.begin();
        //temp.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
        temp.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        renderer.onRender(temp, tex);
        temp.end();
        temp.dispose();
        activeBuffer.end();
    }

    public static TextureRegion blitShader(SpriteBatch sb, ShaderProgram sp) {
        sb.end();
        activeBuffer.end();
        TextureRegion tex = ImageHelper.getBufferTexture(activeBuffer);
        SpriteBatch temp = new SpriteBatch();
        ImageHelper.beginBuffer(shaderBuffer);
        temp.begin();
        temp.setShader(sp);
        //temp.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
        temp.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        temp.draw(tex, 0, 0);
        temp.end();
        temp.dispose();
        shaderBuffer.end();
        TextureRegion ret = ImageHelper.getBufferTexture(shaderBuffer);
        ImageHelper.beginBuffer(activeBuffer);
        sb.begin();
        return ret;
    }

    private static void swapBuffers() {
        if (activeBuffer == frontBuffer) {
            activeBuffer = backBuffer;
        } else {
            activeBuffer = frontBuffer;
        }
    }

    private static void startBuffer(SpriteBatch sb) {
        sb.end();
        ImageHelper.beginBuffer(activeBuffer);
        sb.begin();
    }

    private static void endBuffer(SpriteBatch sb) {
        sb.end();
        activeBuffer.end();
        sb.begin();
    }

    private static void draw(SpriteBatch sb, TextureRegion r) {
        Color origColor = sb.getColor();
        int src = sb.getBlendSrcFunc();
        int dst = sb.getBlendDstFunc();
        sb.setColor(Color.WHITE);
        //sb.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
        sb.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sb.draw(r, transform[0] + transform[2] - Settings.WIDTH/2f, transform[1] + transform[3] - Settings.HEIGHT/2f);
        sb.setColor(origColor);
        sb.setBlendFunction(src, dst);
    }

    private static void prepareTransform(AbstractCreature __instance) {
        transform[0] = __instance.drawX;
        transform[1] = __instance.drawY;
        transform[2] = __instance.animX;
        transform[3] = __instance.animY;
        __instance.drawX = Settings.WIDTH/2f;
        __instance.drawY = Settings.HEIGHT/2f;
        __instance.animX = 0;
        __instance.animY = 0;
    }

    private static void undoTransform(AbstractCreature __instance) {
        __instance.drawX = transform[0];
        __instance.drawY = transform[1];
        __instance.animX = transform[2];
        __instance.animY = transform[3];
        transform[0] = 0;
        transform[1] = 0;
        transform[2] = 0;
        transform[3] = 0;
    }

    public static void render(SpriteBatch sb, TextureRegion tex) {
        render(sb, tex, 0, 0, 1, 1, 0);
    }

    public static void render(SpriteBatch sb, TextureRegion tex, float offsetX, float offsetY) {
        render(sb, tex, offsetX, offsetY, 1, 1, 0);
    }

    public static void render(SpriteBatch sb, TextureRegion tex, float scale) {
        render(sb, tex, 0, 0, scale, scale, 0);
    }

    public static void render(SpriteBatch sb, TextureRegion tex, float offsetX, float offsetY, float scale, float angle) {
        render(sb, tex, offsetX, offsetY, scale, scale, angle);
    }

    public static void render(SpriteBatch sb, TextureRegion tex, float offsetX, float offsetY, float scaleX, float scaleY, float angle) {
        int w = tex.getRegionWidth();
        int h = tex.getRegionHeight();
        int w2 = (int) (w/2f);
        int h2 = (int) (h/2f);
        sb.draw(tex, offsetX, offsetY, w2, h2, w, h, scaleX, scaleY, angle);
        //Texture tex2 = tex.getTexture();
        //sb.draw(tex2, offsetX, offsetY, w2, h2, w, h, scaleX, scaleY, angle, 0, 0, tex2.getWidth(), tex2.getHeight(), false, true);
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "render")
    @SpirePatch2(clz = CustomMonster.class, method = "render")
    public static class RenderTime {
        @SpirePrefixPatch
        public static void onAtStart(AbstractMonster __instance, SpriteBatch sb) {
            beginCapture(__instance, sb);
        }

        @SpireInsertPatch(locator = Locator.class)
        public static void offBeforeIntents(AbstractMonster __instance, SpriteBatch sb) {
            endCapture(__instance, sb);
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new SuperFieldAccessMatcher(AbstractCreature.class, "isDying");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "render")
    public static class RenderTimePlayer {
        public static class OnLocator extends SpireInsertLocator {
            @SpireInsertPatch(locator = OnLocator.class)
            public static void onBeforeImage(AbstractPlayer __instance, SpriteBatch sb) {
                beginCapture(__instance, sb);
            }

            @SpireInsertPatch(locator = OffLocator.class)
            public static void offBeforeHitbox(AbstractPlayer __instance, SpriteBatch sb) {
                endCapture(__instance, sb);
            }

            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "renderCorpse");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }

        public static class OffLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.MethodCallMatcher(Hitbox.class, "render");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }
}
