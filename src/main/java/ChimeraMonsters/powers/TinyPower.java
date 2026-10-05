package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.patches.CreatureRenderPatches;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.powers.interfaces.MultiIntentPower;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class TinyPower extends AbstractInternalLogicPower implements RenderModifierPower, IntentInterceptingPower, MultiIntentPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(TinyPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final float SCALE = 0.66f;
    private static final float SPACE = 48f;
    private EnemyMoveInfo replacedMove;
    private boolean primed;
    private float secondAngle;

    public TinyPower(AbstractCreature owner) {
        super(POWER_ID, owner, -1);
        owner.hb_w *= SCALE;
        owner.hb_h *= SCALE;
        owner.hb.width *= SCALE;
        owner.hb.height *= SCALE;
        owner.healthBarUpdatedEvent();
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        render(sb, tex, 0f, -CreatureRenderPatches.currentTransform()[4]*(1f-SCALE), SCALE, SCALE);
        secondAngle += Gdx.graphics.getDeltaTime() * 150.0F;
    }

    @Override
    public float animationRate() {
        return 1.5f;
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        return 1f;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        if (!(owner instanceof AbstractMonster)) {
            return;
        }
        AbstractMonster ownerMon = (AbstractMonster) owner;
        if (replacedMove == null) {
            ownerMon.rollMove();
            return;
        }
        if (primed) {
            overrideMove(owner, replacedMove);
        } else {
            primed = true;
            this.replacedMove = replacedMove;
            ownerMon.rollMove();
        }
    }

    // TODO - Refactor extra intents to not require interceptor logic to allow meshing arbitrary intent count with interceptors
    @Override
    public boolean performIntercept() {
        if (!(owner instanceof AbstractMonster)) {
            return false;
        }
        AbstractMonster ownerMon = (AbstractMonster) owner;
        EnemyMoveInfo curr = getMove(owner);
        addToBot(new DoAction(() -> {
            //overrideMove(owner, replacedMove);
            ownerMon.nextMove = replacedMove.nextMove;
            ownerMon.takeTurn();
        }));
        addToBot(new DoAction(() -> {
            primed = false;
            ownerMon.nextMove = curr.nextMove;
            ownerMon.takeTurn();
            //overrideMove(owner, curr);
        }));
        return true;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }

    @Override
    public void preIntentRender(SpriteBatch sb) {
        EnemyMoveInfo curr = getMove(owner);

        if (curr == null || replacedMove == null) {
            return;
        }

        offsetIntentHitbox(owner, -SPACE, 0f);
        renderFullIntent(owner, sb, replacedMove, angleCheck(replacedMove, secondAngle));
        offsetIntentHitbox(owner, SPACE*2f, 0f);
    }

    @Override
    public void postIntentRender(SpriteBatch sb) {
        if (replacedMove == null) {
            return;
        }
        offsetIntentHitbox(owner, -SPACE, 0f);
    }
}
