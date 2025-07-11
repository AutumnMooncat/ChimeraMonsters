package ChimeraMonsters.patches;

import basemod.abstracts.CustomCard;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.RenderCardDescriptors;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import javassist.CtBehavior;

import java.util.List;

public class TypeOverridePatch {

    @SpirePatch(clz= AbstractCard.class, method=SpirePatch.CLASS)
    public static class TypeOverrideField {
        public static SpireField<String> typeOverride = new SpireField<>(() -> null);
    }

    @SpirePatch(clz = RenderCardDescriptors.Text.class, method = "Insert")
    public static class OverrideTypeRenderPatch {

        @SpireInsertPatch(locator = Locator.class)
        public static void OverrideType(AbstractCard ___card, SpriteBatch sb, String[] text) {
            if (TypeOverrideField.typeOverride.get(___card) != null) {
                text[0] = TypeOverrideField.typeOverride.get(___card);
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher(List.class, "add");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(clz = RenderCardDescriptors.Frame.class, method = "Insert")
    public static class OverrideTypeSizePatch {

        @SpireInsertPatch(locator = Locator.class, localvars = {"typeText", "descriptors"})
        public static void OverrideType(AbstractCard ___card, SpriteBatch sb, float x, float y, float[] tOffset, float[] tWidth, @ByRef String[] typeText, @ByRef List<String>[] descriptors) {
            if (TypeOverrideField.typeOverride.get(___card) != null) {
                typeText[0] = TypeOverrideField.typeOverride.get(___card);
                GlyphLayout gl = new GlyphLayout();
                FontHelper.cardTypeFont.getData().setScale(1.0F);
                gl.setText(FontHelper.cardTypeFont, typeText[0]);
                tOffset[0] = (gl.width - 38.0F * Settings.scale) / 2.0F;
                tWidth[0] = (gl.width - 0.0F) / (32.0F * Settings.scale);
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher(List.class, "add");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "renderType")
    public static class WorkOnBaseGameCardsPlz {
        @SpireInsertPatch(locator = Locator.class, localvars = {"text"})
        public static void plz(AbstractCard __instance, @ByRef String[] text) {
            if (!(__instance instanceof CustomCard) && TypeOverrideField.typeOverride.get(__instance) != null) {
                text[0] = TypeOverrideField.typeOverride.get(__instance);
            }
        }
        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.MethodCallMatcher(FontHelper.class, "renderRotatedText");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }
}
