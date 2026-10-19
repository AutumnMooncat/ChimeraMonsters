package ChimeraMonsters.patches;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.SkeletonModifierPower;
import ChimeraMonsters.util.DataUtil;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonMeshRenderer;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.MeshAttachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class SkeletonPatches {
    private static final String ID = ChimeraMonstersMod.makeID(SkeletonPatches.class.getSimpleName());
    public static final String SKIP_RENDER_KEY = ID+":skipRender";

    public static boolean shouldRenderBone(PolygonSpriteBatch batch, Slot slot, Attachment attachment, float[] vertices, short[] triangles) {
        if (DataUtil.getData(slot, SKIP_RENDER_KEY) != null || DataUtil.getData(slot.getBone(), SKIP_RENDER_KEY) != null) return false;

        AbstractCreature owner = Fields.owner.get(slot.getSkeleton());
        if (owner == null) return true;

        TextureRegion original = null;
        if (attachment instanceof RegionAttachment) {
            original = ((RegionAttachment) attachment).getRegion();
        } else if (attachment instanceof MeshAttachment) {
            original = ((MeshAttachment) attachment).getRegion();
        }
        if (original == null) return true;

        boolean ret = true;
        SkeletonModifierPower.RenderArgs args = new SkeletonModifierPower.RenderArgs(original, vertices, triangles, 0, 0);
        for (AbstractPower power : owner.powers) {
            if (power instanceof SkeletonModifierPower) {
                ret &= ((SkeletonModifierPower) power).shouldRender(batch, args, slot);
            }
        }
        return ret;
    }

    @SpirePatch2(clz = SkeletonMeshRenderer.class, method = "draw", paramtypez = {PolygonSpriteBatch.class, Skeleton.class})
    public static class ThisIsAwful {
        @SpireInstrumentPatch
        public static ExprEditor plz() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(PolygonSpriteBatch.class.getName()) && m.getMethodName().equals("draw")) {
                        m.replace(String.format("if (%s.shouldRenderBone(batch, slot, attachment, vertices, triangles)) {$_ = $proceed($$); }", SkeletonPatches.class.getName()));
                    }
                }
            };
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                int[] found = LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(PolygonSpriteBatch.class, "draw"));
                return new int[]{found[0]};
            }
        }
    }

    @SpirePatch2(clz = AbstractCreature.class, method = "loadAnimation")
    public static class StoreReference {
        @SpirePostfixPatch
        public static void plz(AbstractCreature __instance) {
            Skeleton skel = ReflectionHacks.getPrivate(__instance, AbstractCreature.class, "skeleton");
            Fields.owner.set(skel, __instance);
        }
    }

    @SpirePatch2(clz = Skeleton.class, method = SpirePatch.CLASS)
    public static class Fields {
        public static SpireField<AbstractCreature> owner = new SpireField<>(() -> null);
    }
}
