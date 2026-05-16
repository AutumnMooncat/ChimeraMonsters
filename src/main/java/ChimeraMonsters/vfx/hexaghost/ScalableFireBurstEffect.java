package ChimeraMonsters.vfx.hexaghost;

import com.megacrit.cardcrawl.vfx.FireBurstParticleEffect;

public class ScalableFireBurstEffect extends FireBurstParticleEffect {
    public ScalableFireBurstEffect(float x, float y, float scale) {
        super(x, y);
        this.scale *= scale;
    }
}
