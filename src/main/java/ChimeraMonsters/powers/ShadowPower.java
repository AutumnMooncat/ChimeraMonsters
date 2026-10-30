package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.MakeTemporaryIntentAction;
import ChimeraMonsters.orbs.MonsterDark;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.MonsterOrbHelper;
import ChimeraMonsters.util.ShaderCompiler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.megacrit.cardcrawl.actions.animations.AnimateShakeAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class ShadowPower extends AbstractInternalLogicPower implements RenderModifierPower, IntentInterceptingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(ShadowPower.class.getSimpleName());
    private static final ShaderProgram sp = ShaderCompiler.makeFrag(ChimeraMonstersMod.makePath("shaders/shadow.frag"));
    private static final ShaderProgram sp2 = ShaderCompiler.makeFrag(ChimeraMonstersMod.makePath("shaders/aberration.frag"));
    private static final ShaderProgram sp3 = ShaderCompiler.makeFrag(ChimeraMonstersMod.makePath("shaders/sobel.frag"));
    private static final Color TEST_COLOR = new Color(0.8f, 0.8f, 0.8f, 0.8f);
    public int cooldown, baseCooldown;

    public ShadowPower(AbstractCreature owner) {
        super(POWER_ID, owner, -1);
        // Extra turn to build back up to 30 damage
        this.cooldown = 4;
        this.baseCooldown = 5;
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        Color origColor = sb.getColor();
        sb.setColor(Color.WHITE);
        //render(sb, tex, 20, 50, 1.1f, 10);
        render(sb, tex);
        blitShader(sb, tex, sp);
        sb.setColor(origColor);
    }

    @Override
    public void otherIntentPicked(EnemyMoveInfo nextMove) {
        if (cooldown > 0) {
            cooldown--;
        }
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        return cooldown == 0 ? 1 : 0;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        overrideMove(owner, MonsterOrbHelper.makeEvokeMove(1));
    }

    @Override
    public boolean performIntercept() {
        cooldown = baseCooldown;
        addToBot(new MonsterOrbHelper.EvokeMonsterOrbAction(owner, 1));
        addToBot(new MakeTemporaryIntentAction(owner, MonsterOrbHelper.makeChannelMove(1), pl -> {
            addToBot(new AnimateShakeAction(pl.owner, 1f, 0.1f));
            addToBot(new MonsterOrbHelper.MonsterChannelAction(owner, new MonsterDark()));
        }));
        return restoreReplacedMove(owner);
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }
}
