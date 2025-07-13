package ChimeraMonsters.vfx;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;

public class BigExplosionVFX extends AbstractGameAction {
    private final ScreenShake.ShakeIntensity intensity;
    private final ScreenShake.ShakeDur dur;

    public BigExplosionVFX(AbstractCreature target) {
        this(target, ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.MED);
    }

    public BigExplosionVFX(AbstractCreature target, ScreenShake.ShakeIntensity intensity, ScreenShake.ShakeDur dur) {
        this.target = target;
        this.intensity = intensity;
        this.dur = dur;
    }

    @Override
    public void update() {
        if (target != null) {
            AbstractDungeon.effectsQueue.add(new ExplosionSmallEffect(target.hb.cX, target.hb.cY));
            AbstractDungeon.effectsQueue.add(new BurnToAshEffect(target.hb.cX, target.hb.cY));
            CardCrawlGame.screenShake.shake(intensity, dur, false);
        }
        this.isDone = true;
    }
}
