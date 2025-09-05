package ChimeraMonsters.powers.interfaces;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.CreatureRenderPatches;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public interface RenderModifierPower {
    default void onRender(SpriteBatch sb, TextureRegion tex) {
        Color origColor = sb.getColor();
        sb.setColor(Color.WHITE);
        render(sb, tex);
        sb.setColor(origColor);
    }

    default TextureRegion blitShader(SpriteBatch sb, TextureRegion tex, ShaderProgram sp) {
        if (!ChimeraMonstersMod.enableShaders) {
            return tex;
        }
        TextureRegion ret = CreatureRenderPatches.blitShader(sb, sp);
        render(sb, ret);
        return ret;
    }

    default void render(SpriteBatch sb, TextureRegion tex) {
        CreatureRenderPatches.render(sb, tex, 0, 0, 1, 1, 0);
    }

    default void render(SpriteBatch sb, TextureRegion tex, float offsetX, float offsetY) {
        CreatureRenderPatches.render(sb, tex, offsetX, offsetY, 1, 1, 0);
    }

    default void render(SpriteBatch sb, TextureRegion tex, float scale) {
        CreatureRenderPatches.render(sb, tex, 0, 0, scale, scale, 0);
    }

    default void render(SpriteBatch sb, TextureRegion tex, float offsetX, float offsetY, float scale, float angle) {
        CreatureRenderPatches.render(sb, tex, offsetX, offsetY, scale, scale, angle);
    }

    default void render(SpriteBatch sb, TextureRegion tex, float offsetX, float offsetY, float scaleX, float scaleY, float angle) {
        CreatureRenderPatches.render(sb, tex, offsetX, offsetY, scaleX, scaleY, angle);
    }
}
