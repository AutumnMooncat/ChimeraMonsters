package ChimeraMonsters.powers.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface OnIntentRenderPower {
    void preIntentRender(SpriteBatch sb);
    void postIntentRender(SpriteBatch sb);
}
