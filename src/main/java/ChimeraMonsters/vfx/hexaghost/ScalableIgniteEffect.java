package ChimeraMonsters.vfx.hexaghost;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.FireBurstParticleEffect;
import com.megacrit.cardcrawl.vfx.combat.LightFlareParticleEffect;

public class ScalableIgniteEffect extends AbstractGameEffect {
    private static final int COUNT = 25;
    private float x;
    private float y;

    public ScalableIgniteEffect(float x, float y, float scale) {
        this.x = x;// 14
        this.y = y;// 15
        this.scale = scale;
    }// 16

    public void update() {
        for(int i = 0; i < 25; ++i) {// 19
            AbstractDungeon.effectsQueue.add(new ScalableFireBurstEffect(this.x, this.y, scale));// 20
            AbstractDungeon.effectsQueue.add(new ScalableLightFlareEffect(this.x, this.y, Color.CHARTREUSE, scale));// 21
        }

        this.isDone = true;// 23
    }// 24

    public void render(SpriteBatch sb) {
    }// 29

    public void dispose() {
    }// 34
}