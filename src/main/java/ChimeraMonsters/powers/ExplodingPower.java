package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.patches.MoveManipulationPatches;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;

public class ExplodingPower extends AbstractEasyPower implements IntentInterceptingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(ExplodingPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int dmg;
    private boolean primed;

    public ExplodingPower(AbstractCreature owner, int turns, int dmg) {
        super(POWER_ID, NAME, PowerType.BUFF, true, owner, turns);
        this.dmg = dmg;
        priority = -5;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (this.amount == 1) {
            this.description = DESCRIPTIONS[3] + dmg + DESCRIPTIONS[2];
        } else {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + dmg + DESCRIPTIONS[2];
        }
    }

    @Override
    public void renderAmount(SpriteBatch sb, float x, float y, Color c) {
        super.renderAmount(sb, x, y, c);
        FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont, Integer.toString(dmg), x, y + 15.0F * Settings.scale, fontScale, c);
    }

    @Override
    public void duringTurn() {
        if (amount > 1) {
            addToBot(new ReducePowerAction(owner, owner, this, 1));
            addToBot(new DoAction(() -> {
                if (amount == 1) {
                    primed = true;
                    startFlashing();
                    MoveManipulationPatches.removeAndResetInterceptor((AbstractMonster) owner);
                    MoveManipulationPatches.applyInterceptor((AbstractMonster) owner, ExplodingPower.this, getMove(owner));
                }
            }));
        } else if (!owner.isDying && owner instanceof AbstractMonster) {
            addToBot(new VFXAction(new ExplosionSmallEffect(owner.hb.cX, owner.hb.cY), 0.1F));
            addToBot(new SuicideAction((AbstractMonster) owner));
            DamageInfo damageInfo = new DamageInfo(owner, dmg, DamageInfo.DamageType.THORNS);
            addToBot(new DamageAction(AbstractDungeon.player, damageInfo, AbstractGameAction.AttackEffect.FIRE, true));
        }
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        if (primed) {
            return 1f;
        }
        return 0f;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        EnemyMoveInfo newMove = new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.UNKNOWN, -1 , 0, false);
        overrideMove(owner, newMove);
    }

    @Override
    public boolean performIntercept() {
        return false;
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }
}
