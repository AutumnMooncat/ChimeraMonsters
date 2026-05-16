package ChimeraMonsters.vfx.hexaghost;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.vfx.combat.LightFlareParticleEffect;

public class ScalableLightFlareEffect extends LightFlareParticleEffect {
    public ScalableLightFlareEffect(float x, float y, Color color, float scale) {
        super(x, y, color);
        this.scale *= scale;
    }
}
