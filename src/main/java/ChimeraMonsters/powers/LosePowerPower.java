package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class LosePowerPower extends AbstractEasyPower {
    public static String TEXT_ID = ChimeraMonstersMod.makeID(LosePowerPower.class.getSimpleName());
    public static PowerStrings strings = CardCrawlGame.languagePack.getPowerStrings(TEXT_ID);
    private AbstractPower powerToLose;

    public LosePowerPower(AbstractCreature owner, AbstractPower powerToLose, int amount) {
        super(LosePowerPower.TEXT_ID+powerToLose.name, strings.NAME + powerToLose.name, PowerType.DEBUFF, false, owner, amount);
        this.img = powerToLose.img;
        this.region48 = powerToLose.region48;
        this.region128 = powerToLose.region128;
        this.powerToLose = powerToLose;
        updateDescription();
    }

    @Override
    public void renderIcons(SpriteBatch sb, float x, float y, Color c) {
        super.renderIcons(sb, x, y, Color.RED.cpy());
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        flash();
        addToBot(new ReducePowerAction(owner, owner, powerToLose.ID, amount));
        addToBot(new RemoveSpecificPowerAction(owner, owner, this.ID));
    }

    @Override
    public void updateDescription() {
        if (powerToLose == null) {
            description = "???";
        } else {
            description = strings.DESCRIPTIONS[0] + amount + strings.DESCRIPTIONS[1] + powerToLose.name + strings.DESCRIPTIONS[2];
        }
    }
}
