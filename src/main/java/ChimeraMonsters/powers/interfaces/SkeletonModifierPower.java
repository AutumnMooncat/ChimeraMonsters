package ChimeraMonsters.powers.interfaces;

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Slot;

public interface SkeletonModifierPower {
    class RenderArgs {
        public TextureRegion region;
        public float[] polygonVertices;
        public short[] polygonTriangles;
        public int verticesOffset;
        public int trianglesOffset;

        public RenderArgs(TextureRegion region, float[] polygonVertices, short[] polygonTriangles, int verticesOffset, int trianglesOffset) {
            this.region = region;
            this.polygonVertices = polygonVertices;
            this.polygonTriangles = polygonTriangles;
            this.verticesOffset = verticesOffset;
            this.trianglesOffset = trianglesOffset;
        }
    }

    boolean shouldRender(PolygonSpriteBatch psb, RenderArgs args, Slot slot);

    default void manualRender(PolygonSpriteBatch psb, RenderArgs args) {
        psb.draw(args.region.getTexture(), args.polygonVertices, args.verticesOffset, args.polygonVertices.length, args.polygonTriangles, args.trianglesOffset, args.polygonTriangles.length);
    }

    default boolean boneIsNamed(Bone bone, String name) {
        return bone != null && bone.getData().getName().equals(name);
    }

    default boolean boneContainsName(Bone bone, String name) {
        return bone != null && bone.getData().getName().contains(name);
    }

    default boolean slotIsNamed(Slot slot, String name) {
        return boneIsNamed(slot.getBone(), name);
    }

    default boolean slotContainsName(Slot slot, String name) {
        return boneContainsName(slot.getBone(), name);
    }

    default boolean parentIsNamed(Bone bone, String name) {
        if (bone == null || bone.getParent() == null) return false;
        return boneIsNamed(bone.getParent(), name) || parentIsNamed(bone.getParent(), name);
    }

    default boolean parentIsNamed(Slot slot, String name) {
        return parentIsNamed(slot.getBone(), name);
    }

    default boolean parentContainsName(Bone bone, String name) {
        if (bone == null || bone.getParent() == null) return false;
        return boneContainsName(bone.getParent(), name) || parentContainsName(bone.getParent(), name);
    }

    default boolean parentContainsName(Slot slot, String name) {
        return parentContainsName(slot.getBone(), name);
    }

    default float smallerRatio(TextureRegion current, TextureRegion original) {
        return Math.min(xRatio(current, original), yRatio(current, original));
    }

    default float largerRatio(TextureRegion current, TextureRegion original) {
        return Math.max(xRatio(current, original), yRatio(current, original));
    }

    default float lessManipulationRatio(TextureRegion current, TextureRegion original) {
        float xRatio = xRatio(current, original);
        float yRatio = yRatio(current, original);
        float xComp = xRatio >= 1 ? xRatio : 1 / xRatio;
        float yComp = yRatio >= 1 ? yRatio : 1 / yRatio;
        return xComp > yComp ? yRatio : xRatio;
    }

    default float moreManipulationRatio(TextureRegion current, TextureRegion original) {
        float xRatio = xRatio(current, original);
        float yRatio = yRatio(current, original);
        float xComp = xRatio >= 1 ? xRatio : 1 / xRatio;
        float yComp = yRatio >= 1 ? yRatio : 1 / yRatio;
        return xComp > yComp ? xRatio : yRatio;
    }

    default float xRatio(TextureRegion current, TextureRegion original) {
        return (float) original.getRegionWidth() / current.getRegionWidth();
    }

    default float yRatio(TextureRegion current, TextureRegion original) {
        return (float) original.getRegionHeight() / current.getRegionHeight();
    }

    default float slotXPos(Slot slot) {
        return slot.getSkeleton().getX() + slot.getBone().getWorldX();
    }

    default float slotYPos(Slot slot) {
        return slot.getSkeleton().getY() + slot.getBone().getWorldY();
    }
}
