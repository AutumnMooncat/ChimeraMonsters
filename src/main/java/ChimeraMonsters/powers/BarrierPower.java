package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.TurnStartBlockLossPower;
import ChimeraMonsters.util.ColorUtil;
import ChimeraMonsters.vfx.stance.GenericWrathParticle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.BufferPower;

public class BarrierPower extends AbstractEasyPower implements TurnStartBlockLossPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(BarrierPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private float particleTimer;

    public BarrierPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public void updateParticles() {
        if (owner.currentBlock > 0) {
            particleTimer -= Gdx.graphics.getDeltaTime();
            if (particleTimer <= 0) {
                particleTimer = MathUtils.random(0.15f, 0.25f);
                for(int i = 0; i < 4; ++i) {
                    AbstractDungeon.effectsQueue.add(new GenericWrathParticle(ColorUtil.COBALT, owner.hb));
                }
            }
        }
    }

    @Override
    public void preBlockLoss() {
        if (owner.currentBlock > 0) {
            flash();
            addToBot(new ApplyPowerAction(owner, owner, new BufferPower(owner, amount)));
        }
    }

    @Override
    public void postBlockLoss() {}
}
