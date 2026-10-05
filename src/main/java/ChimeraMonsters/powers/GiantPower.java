package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.CreatureRenderPatches;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class GiantPower extends AbstractInternalLogicPower implements RenderModifierPower, IntentInterceptingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(GiantPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final float SCALE = 1.5f;
    public int cooldown = 1;
    private EnemyMoveInfo replacedMove;

    public GiantPower(AbstractCreature owner) {
        super(POWER_ID, owner, -1);
        owner.hb_w *= SCALE;
        owner.hb_h *= SCALE;
        owner.hb.width *= SCALE;
        owner.hb.height *= SCALE;
        owner.healthBarUpdatedEvent();
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        render(sb, tex, 0f, CreatureRenderPatches.currentTransform()[4]*(SCALE-1f), SCALE, SCALE);
    }

    @Override
    public float animationRate() {
        return 0.66f;
    }

    @Override
    public void otherIntentPicked(EnemyMoveInfo nextMove) {
        if (cooldown > 0) {
            cooldown--;
        }
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        return cooldown == 0 ? 1f : 0f;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        this.replacedMove = replacedMove;
        EnemyMoveInfo info = new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.STUN, -1, 0, false);
        overrideMove(owner, info);
    }

    @Override
    public boolean performIntercept() {
        cooldown = 1;
        addToBot(new TextAboveCreatureAction(owner, TextAboveCreatureAction.TextType.STUNNED));
        if (replacedMove != null) {
            setMove(owner, replacedMove);
            replacedMove = null;
            return true;
        }
        return false;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }
}
