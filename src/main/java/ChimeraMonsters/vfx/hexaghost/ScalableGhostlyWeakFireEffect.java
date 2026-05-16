package ChimeraMonsters.vfx.hexaghost;

import com.megacrit.cardcrawl.vfx.GhostlyWeakFireEffect;

public class ScalableGhostlyWeakFireEffect extends GhostlyWeakFireEffect {
    public ScalableGhostlyWeakFireEffect(float x, float y, float scale) {
        super(x, y);
        this.scale *= scale;
    }
}
