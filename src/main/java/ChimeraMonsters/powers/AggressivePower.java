package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.DataUtil;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class AggressivePower extends AbstractInternalLogicPower implements IntentInterceptingPower, RenderModifierPower, DataUtil {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(AggressivePower.class.getSimpleName());
    private static final String LAST_ATTACK_KEY = POWER_ID+":lastAttack";

    public AggressivePower(AbstractCreature owner) {
        super(POWER_ID, owner, -1);
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        if (intendedMove.baseDamage > -1 || intendedMove.intent == AbstractMonster.Intent.STUN) {
            return 0f;
        }
        EnemyMoveInfo lastAttack = getData(LAST_ATTACK_KEY);
        return lastAttack == null ? 0f : 0.5f;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        EnemyMoveInfo lastAttack = getData(LAST_ATTACK_KEY);
        overrideMove(owner, lastAttack);
    }

    @Override
    public void otherIntentPicked(EnemyMoveInfo nextMove) {
        if (nextMove.baseDamage > -1 && nextMove.nextMove > 0) {
            setData(LAST_ATTACK_KEY, nextMove);
        }
    }

    @Override
    public boolean performIntercept() {
        takeTurn(owner);
        return true;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        Color origColor = sb.getColor();
        sb.setColor(new Color(1f,0.95f,0.95f,1f));
        render(sb, tex);
        sb.setColor(origColor);
    }

    @Override
    public Object getHandler() {
        return getDataHandler();
    }
}
