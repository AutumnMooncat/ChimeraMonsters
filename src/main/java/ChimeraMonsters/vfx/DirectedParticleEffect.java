package ChimeraMonsters.vfx;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.helpers.Hitbox;

public class DirectedParticleEffect extends ParticleEffect {
    public DirectedParticleEffect(Color c, float x, float y, float dx, float dy) {
        super(c, x, y);
        this.vX = dx;
        this.vY = dy;
    }

    public DirectedParticleEffect(Color c, Hitbox hb, float dx, float dy) {
        super(c, hb);
        this.vX = dx;
        this.vY = dy;
    }
}