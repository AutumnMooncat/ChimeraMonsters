package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.ImageHelper;
import ChimeraMonsters.util.matchers.SuperFieldAccessMatcher;
import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;

public class CreatureRenderPatches {
    private static final FrameBuffer frontBuffer = ImageHelper.createBuffer();
    private static final FrameBuffer backBuffer = ImageHelper.createBuffer();
    private static FrameBuffer activeBuffer = frontBuffer;
    private static boolean capturing = false;
    private static boolean drawnToBuffer = false;

    public static void beginCapture(AbstractCreature __instance, SpriteBatch sb) {
        capturing = false;
        if (__instance.powers.stream().anyMatch(p -> p instanceof RenderModifierPower)) {
            capturing = true;
            startBuffer(sb);
        }
    }

    public static void endCapture(AbstractCreature __instance, SpriteBatch sb) {
        if (capturing) {
            endBuffer(sb);
            for (AbstractPower power : __instance.powers) {
                if (power instanceof RenderModifierPower) {
                    TextureRegion tex = ImageHelper.getBufferTexture(activeBuffer);
                    swapBuffers();
                    startBuffer(sb);
                    ShaderProgram sp = ((RenderModifierPower) power).getShader();
                    ShaderProgram origShader = null;
                    if (sp != null && ChimeraMonstersMod.enableShaders) {
                        origShader = sb.getShader();
                        sb.setShader(sp);
                    }
                    ((RenderModifierPower) power).onRender(sb, tex);
                    if (origShader != null) {
                        sb.setShader(origShader);
                    }
                    endBuffer(sb);
                }
            }
            draw(sb);
        }
    }

    public static void swapBuffers() {
        if (activeBuffer == frontBuffer) {
            activeBuffer = backBuffer;
        } else {
            activeBuffer = frontBuffer;
        }
    }

    public static void startBuffer(SpriteBatch sb) {
        sb.end();
        ImageHelper.beginBuffer(activeBuffer);
        sb.begin();
    }

    public static void endBuffer(SpriteBatch sb) {
        sb.end();
        activeBuffer.end();
        sb.begin();
        drawnToBuffer = true;
    }

    public static void draw(SpriteBatch sb) {
        if (drawnToBuffer) {
            TextureRegion r = ImageHelper.getBufferTexture(activeBuffer);
            Color origColor = sb.getColor();
            sb.setColor(Color.WHITE);
            sb.draw(r, 0, 0);
            sb.setColor(origColor);
            drawnToBuffer = false;
            capturing = false;
        }
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
