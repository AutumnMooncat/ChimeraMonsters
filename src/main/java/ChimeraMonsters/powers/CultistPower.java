package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.EnemyMoveInfoFields;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.powers.interfaces.SkeletonModifierPower;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Slot;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.monsters.beyond.*;
import com.megacrit.cardcrawl.monsters.city.SnakePlant;
import com.megacrit.cardcrawl.monsters.exordium.*;
import com.megacrit.cardcrawl.powers.RitualPower;

public class CultistPower extends AbstractInternalLogicPower implements IntentInterceptingPower, SkeletonModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(CultistPower.class.getSimpleName());
    public static final TextureAtlas cultistAtlas = new TextureAtlas(Gdx.files.internal("images/monsters/theBottom/cultist/skeleton.atlas"));
    private boolean primed, activated;

    public CultistPower(AbstractCreature owner, int amount) {
        super(POWER_ID, owner, amount);
        priority = -5;
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        return primed || activated ? 0 : 1;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        EnemyMoveInfo newMove = new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.BUFF, 0, 0, true);
        EnemyMoveInfoFields.name.set(newMove, Cultist.MOVES[2]);
        overrideMove(owner, newMove);
        primed = true;
    }

    @Override
    public boolean performIntercept() {
        int temp = MathUtils.random(1, 10);
        playSfx();
        if (temp < 4) {
            addToBot(new TalkAction(owner, Cultist.DIALOG[0], 1.0F, 2.0F));
        } else if (temp < 7) {
            addToBot(new TalkAction(owner, Cultist.DIALOG[1], 1.0F, 2.0F));
        }
        addToBot(new ApplyPowerAction(owner, owner, new RitualPower(owner, amount, false)));

        activated = true;
        return restoreReplacedMove(owner);
    }

    @Override
    public void onFinishedThisIntercept() {
        primed = false;
    }

    private void playSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            addToBot(new SFXAction("VO_CULTIST_1A"));
        } else if (roll == 1) {
            addToBot(new SFXAction("VO_CULTIST_1B"));
        } else {
            addToBot(new SFXAction("VO_CULTIST_1C"));
        }
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }

    @Override
    public boolean shouldRender(PolygonSpriteBatch psb, RenderArgs args, Slot slot) {
        if (owner instanceof GiantHead || owner instanceof TimeEater || owner instanceof OrbWalker) {
            if (parentIsNamed(slot, "head")) return false;
        }
        if (slotContainsName(slot, "eye") || slotContainsName(slot, "mouth")) return false;
        if (!slotIsNamed(slot, "head")) return true;

        Bone bone = slot.getBone();
        TextureRegion tex = cultistAtlas.findRegion("images/head");
        float xPivot = 0.5f;
        float yPivot = slot.getSkeleton().getFlipY() ? 0.25f : 0.75f;
        float scalar = largerRatio(tex, args.region);
        float rot = bone.getRotation();
        if (owner instanceof SnakePlant) {
            xPivot = slot.getSkeleton().getFlipX() ? 0.4f : 0.6f;
            rot = -45f;
        } else if (owner instanceof GremlinNob) {
            yPivot = 0.5f;
            scalar = 1f;
        } else if (owner instanceof GiantHead) {
            yPivot = 0.5f;
            scalar = lessManipulationRatio(tex, args.region);
        } else if (owner instanceof GremlinThief) {
            yPivot = slot.getSkeleton().getFlipY() ? 0.3f : 0.7f;
            scalar = 0.8f;
            rot -= 10f;
        } else if (owner instanceof GremlinTsundere) {
            scalar = 0.7f;
        } else if (owner instanceof GremlinWarrior) {
            scalar = 0.8f;
            rot -= 30f;
        } else if (owner instanceof GremlinWizard) {
            scalar = 0.9f;
            rot -= 10f;
        } else if (owner instanceof Repulsor) {
            scalar = smallerRatio(tex, args.region);
        } else if (owner instanceof Nemesis) {
            scalar = 2f;
        }

        SpriteBatch temp = new SpriteBatch();
        psb.end();
        temp.begin();
        //temp.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
        temp.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        temp.draw(tex,
                slotXPos(slot) - tex.getRegionWidth()*xPivot,
                slotYPos(slot) - tex.getRegionHeight()*yPivot,
                tex.getRegionWidth()*xPivot,
                tex.getRegionHeight()*yPivot,
                tex.getRegionWidth(),
                tex.getRegionHeight(),
                bone.getScaleX() * scalar,
                bone.getScaleY() * scalar,
                rot);
        temp.end();
        temp.dispose();
        psb.begin();
        return false;
    }
}
