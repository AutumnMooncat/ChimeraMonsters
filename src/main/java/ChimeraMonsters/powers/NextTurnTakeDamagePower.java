package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.DamageInfoFields;
import ChimeraMonsters.powers.interfaces.MonsterAtPlayerStartOfTurnPower;
import ChimeraMonsters.util.ColorUtil;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class NextTurnTakeDamagePower extends AbstractEasyPower implements NonStackablePower, MonsterAtPlayerStartOfTurnPower, HealthBarRenderPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(NextTurnTakeDamagePower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final Color direct = ColorUtil.GOLD.cpy();
    private static final Color indirect = ColorUtil.SILVER.cpy();
    private static final Color loss = ColorUtil.PHLOX.cpy();
    public final AbstractGameAction.AttackEffect effect;
    public final DamageInfo info;

    public NextTurnTakeDamagePower(AbstractCreature owner, DamageInfo info, AbstractGameAction.AttackEffect effect) {
        super(POWER_ID, NAME, NeutralPowertypePatch.NEUTRAL, false, owner, info.output);
        this.info = info;
        this.effect = effect;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (info == null) {
            this.description = "???";
        } else {
            if (info.type == DamageInfo.DamageType.NORMAL) {
                this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
            } else if (info.type == DamageInfo.DamageType.HP_LOSS) {
                this.description = DESCRIPTIONS[4] + amount + DESCRIPTIONS[5];
            } else {
                this.description = DESCRIPTIONS[2] + amount + DESCRIPTIONS[3];
            }
        }
    }

    @Override
    public void atPlayerStartOfTurn() {
        flash();
        addToBot(new DamageAction(owner, info, effect));
        addToBot(new RemoveSpecificPowerAction(owner, owner, this));
    }

    @Override
    public int getHealthBarAmount() {
        return amount;
    }

    @Override
    public Color getColor() {
        return info.type == DamageInfo.DamageType.NORMAL ? direct : info.type == DamageInfo.DamageType.HP_LOSS ? loss : indirect;
    }
}