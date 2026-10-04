package ChimeraMonsters.vfx;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.helpers.Hitbox;

public class DirectedParticleEffect extends ParticleEffect {
    public DirectedParticleEffect(Color c, float x, float y, float dx, float dy, float posScale, float velScale) {
        super(c, x, y, posScale, velScale);
        this.vX = dx;
        this.vY = dy;
    }

    public DirectedParticleEffect(Color c, Hitbox hb, float dx, float dy, float posScale, float velScale) {
        super(c, hb, posScale, velScale);
        this.vX = dx;
        this.vY = dy;
    }
}