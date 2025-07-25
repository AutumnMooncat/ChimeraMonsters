package ChimeraMonsters.powers.interfaces;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public interface RenderModifierPower {
    default void onRender(SpriteBatch sb, TextureRegion tex) {
        Color origColor = sb.getColor();
        sb.setColor(Color.WHITE);
        sb.draw(tex, 0, 0);
        sb.setColor(origColor);
    }

    default ShaderProgram getShader() {
        return null;
    }
}
