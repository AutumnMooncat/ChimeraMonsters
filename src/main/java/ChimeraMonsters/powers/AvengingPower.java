package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.powers.interfaces.OnOtherMonsterKilledPower;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class AvengingPower extends AbstractEasyPower implements IntentInterceptingPower, OnOtherMonsterKilledPower, RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(AvengingPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int kills;

    public AvengingPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        priority = -5;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public float interceptRate(EnemyMoveInfo intendedMove) {
        return 0f;
    }

    @Override
    public void setInterceptIntent(EnemyMoveInfo replacedMove) {
        EnemyMoveInfo newMove = new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.BUFF, 0, 0, false);
        overrideMove(owner, newMove, true);
    }

    @Override
    public boolean performIntercept() {
        addToBot(new ApplyPowerAction(owner, owner, new StrengthPower(owner, amount)));
        return restoreReplacedMove(owner);
    }

    @Override
    public boolean setFollowupInterceptionIntent() {
        return false;
    }

    @Override
    public void onMonsterKilled(AbstractMonster killed) {
        kills++;
        flash();
        addToBot(new DoAction(() -> {
            // TODO multi intent check
            removeAndResetInterceptor(owner);
            applyInterceptor(owner, this, getMove(owner));
        }));
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        Color origColor = sb.getColor();
        float val = 0.95f - (0.1f * kills);
        sb.setColor(new Color(1f, val, val, 1f));
        render(sb, tex);
        sb.setColor(origColor);
    }
}
