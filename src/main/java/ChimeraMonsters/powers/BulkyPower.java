package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.ShakeScreenAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class BulkyPower extends AbstractInternalLogicPower implements RenderModifierPower, IntentInterceptingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(BulkyPower.class.getSimpleName());
    private final int block, str;
    public int cooldown;
    public int activations;

    public BulkyPower(AbstractCreature owner, int block, int str) {
        super(POWER_ID, owner, -1);
        this.block = block;
        this.str = str;
        cooldown = 1;
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        render(sb, tex, 0f, 0f, 1.15f, 1f, 0f);
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        if (cooldown > 0) return 0f;
        return Math.max(0.2f, 1f - 0.2f*activations);
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        overrideMove(owner, new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.DEFEND_BUFF, -1, 0, false));
    }

    @Override
    public boolean performIntercept() {
        cooldown = 2;
        activations++;
        addToBot(new SFXAction("MONSTER_JAW_WORM_BELLOW"));
        addToBot(new ShakeScreenAction(0.2F, ScreenShake.ShakeDur.SHORT, ScreenShake.ShakeIntensity.MED));
        addToBot(new WaitAction(0.5F));
        addToBot(new ApplyPowerAction(owner, owner, new StrengthPower(owner, str)));
        addToBot(new GainBlockAction(owner, block));
        return restoreReplacedMove(owner);
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }
}
