package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.util.LoopingSoundManager;
import ChimeraMonsters.vfx.stance.GenericStanceAuraEffect;
import ChimeraMonsters.vfx.stance.GenericWrathChangeParticle;
import ChimeraMonsters.vfx.stance.GenericWrathParticle;
import basemod.Pair;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;

public class WrathfulPower extends AbstractEasyPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(WrathfulPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final String loopSFX = "STANCE_LOOP_WRATH";
    private float particleTimer, particleTimer2;
    private static Pair<String, Long> loopKey;

    public WrathfulPower(AbstractCreature owner) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, -1);
        priority = 100;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public void playApplyPowerSfx() {
        if (loopKey != null) {
            this.stopIdleSfx();
        }
        loopKey = LoopingSoundManager.addLoopedSound(loopSFX);
        for(int i = 0; i < 10; ++i) {
            AbstractDungeon.effectsQueue.add(new GenericWrathChangeParticle(GenericWrathChangeParticle.originalColor(), owner.hb.cX));
        }
        AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.SCARLET, true));
        CardCrawlGame.sound.play("STANCE_ENTER_WRATH", 0f);
    }

    @Override
    public void updateParticles() {
        particleTimer -= Gdx.graphics.getDeltaTime();
        if (particleTimer <= 0) {
            particleTimer = 0.05f;
            AbstractDungeon.effectsQueue.add(new GenericWrathParticle(GenericWrathParticle.originalColor(), owner.hb));
        }

        particleTimer2 -= Gdx.graphics.getDeltaTime();
        if (this.particleTimer2 <= 0) {
            this.particleTimer2 = MathUtils.random(0.3F, 0.4F);
            AbstractDungeon.effectsQueue.add(new GenericStanceAuraEffect(GenericStanceAuraEffect.wrathColor(), owner.hb));
        }
    }

    @Override
    public void onRemove() {
        super.onRemove();
        this.stopIdleSfx();
    }

    public void stopIdleSfx() {
        if (loopKey != null) {
            LoopingSoundManager.stopLoopedSound(loopKey);
        }
    }

    @Override
    public float atDamageFinalGive(float damage, DamageInfo.DamageType type) {
        return damage * 2f;
    }

    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
        return damage * 2f;
    }
}
