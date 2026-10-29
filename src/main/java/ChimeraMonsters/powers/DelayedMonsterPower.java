package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.util.ColorUtil;
import ChimeraMonsters.util.FormatHelper;
import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DelayedMonsterPower extends AbstractEasyPower implements NonStackablePower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(DelayedMonsterPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final AbstractMonster delayedMonster;

    public DelayedMonsterPower(AbstractCreature owner, AbstractMonster delayed, int turns) {
        super(POWER_ID, NAME, NeutralPowertypePatch.NEUTRAL, true, owner, turns);
        this.delayedMonster = delayed;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (delayedMonster == null) {
            description = "???";
            return;
        }
        if (amount == 1) {
            description = DESCRIPTIONS[0];
        } else {
            description = DESCRIPTIONS[1] + amount + DESCRIPTIONS[2];
        }
        description += FormatHelper.prefixWords(delayedMonster.name, "#y") + DESCRIPTIONS[3];
    }

    @Override
    public void atEndOfRound() {
        addToBot(new ReducePowerAction(owner, owner, this, 1));
    }

    @Override
    public void onRemove() {
        AbstractDungeon.getMonsters().addMonster(getSmartPosition(), delayedMonster);
        delayedMonster.tint.color.a = 0;
        delayedMonster.tint.changeColor(ColorUtil.WHITE, 3f);
        delayedMonster.init();
        delayedMonster.showHealthBar();
        delayedMonster.createIntent();
        //delayedMonster.usePreBattleAction();
        //delayedMonster.useUniversalPreBattleAction();
    }

    private int getSmartPosition() {
        int position = 0;
        for(AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!(delayedMonster.drawX > mo.drawX)) {
                break;
            }
            ++position;
        }
        return position;
    }
}
