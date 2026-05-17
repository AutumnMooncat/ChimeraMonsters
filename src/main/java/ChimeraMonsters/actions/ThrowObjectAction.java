package ChimeraMonsters.actions;

import ChimeraMonsters.vfx.VFXContainer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class ThrowObjectAction extends AbstractGameAction {
    //private static final float THROW_TIME = 0.25f;
    public boolean spawned = false;
    private final AbstractGameEffect e;

    public ThrowObjectAction(Texture tex, float scale, Hitbox target, Color color, boolean bounceOff) {
        this.e = VFXContainer.throwEffect(tex, scale, target, color, bounceOff, true);
    }

    public ThrowObjectAction(Texture tex, float scale, Hitbox source, Hitbox target, Color color, boolean bounceOff) {
        this.e = VFXContainer.throwEffect(tex, scale, source, target, color, bounceOff, true);
    }

    public ThrowObjectAction(Texture tex, float scale, float sourceX, float sourceY, float targetX, float targetY, Color color, boolean bounceOff) {
        this.e = VFXContainer.throwEffect(tex, scale, sourceX, sourceY, targetX, targetY, color, bounceOff, true);
    }

    @Override
    public void update() {
        if (!spawned) {
            spawned = true;
            AbstractDungeon.effectList.add(e);
        }
        this.isDone = e.isDone;
    }
}