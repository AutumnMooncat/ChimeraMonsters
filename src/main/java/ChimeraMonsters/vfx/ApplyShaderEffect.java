package ChimeraMonsters.vfx;

import basemod.helpers.ScreenPostProcessorManager;
import basemod.interfaces.ScreenPostProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class ApplyShaderEffect extends AbstractGameEffect {
    private final ScreenPostProcessor spp;

    public ApplyShaderEffect(ScreenPostProcessor spp, float duration) {
        this.spp = spp;
        this.duration = this.startingDuration = duration;
        this.color = new Color();
    }

    @Override
    public void update() {
        if (duration == startingDuration) {
            ScreenPostProcessorManager.addPostProcessor(spp);
        }
        super.update();
        if (isDone) {
            ScreenPostProcessorManager.removePostProcessor(spp);
        }
    }

    @Override
    public void render(SpriteBatch sb) {}

    @Override
    public void dispose() {}
}
