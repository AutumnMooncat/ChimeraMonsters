package ChimeraMonsters.vfx.hexaghost;

import com.megacrit.cardcrawl.vfx.GhostlyFireEffect;

public class ScalableGhostlyFireEffect extends GhostlyFireEffect {
    public ScalableGhostlyFireEffect(float x, float y, float scale) {
        super(x, y);
        this.scale *= scale;
    }
}
