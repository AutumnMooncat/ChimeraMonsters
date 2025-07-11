package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class MonsterDexterityPower extends AbstractModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(MonsterDexterityPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public MonsterDexterityPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        this.loadRegion("dexterity");
        canGoNegative = true;
    }

    public void updateDescription() {
        if (this.amount > 0) {
            this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[2];
            this.type = PowerType.BUFF;
        } else {
            this.description = DESCRIPTIONS[1] + -amount + DESCRIPTIONS[2];
            this.type = PowerType.DEBUFF;
        }
    }

    @Override
    public float modifyMonsterBlock(float blockAmount) {
        return (blockAmount += amount) < 0.0F ? 0.0F : blockAmount;
    }
}
