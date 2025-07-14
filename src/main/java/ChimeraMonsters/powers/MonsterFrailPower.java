package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.MonsterBlockChangingPower;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class MonsterFrailPower extends AbstractEasyPower implements MonsterBlockChangingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(MonsterFrailPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean justApplied;

    public MonsterFrailPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.DEBUFF, true, owner, amount);
        this.loadRegion("frail");
        this.priority = 10;
        this.justApplied = true;
    }

    public void updateDescription() {
        if (amount == 1) {
            description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
        } else {
            description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[2];
        }
    }

    public void atEndOfRound() {
        if (justApplied) {
            justApplied = false;
        } else {
            addToBot(new ReducePowerAction(owner, owner, this, 1));
        }
    }

    @Override
    public float modifyMonsterBlock(float blockAmount) {
        return blockAmount * 0.75f;
    }
}
