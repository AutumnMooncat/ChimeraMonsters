package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.ApplyPowerActionWithFollowup;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.actions.DoIfAction;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.ImpactSparkEffect;

public class BloodlustPower extends AbstractEasyPower implements RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(BloodlustPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int baseAmount;
    private boolean drewBlood;

    public BloodlustPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        this.priority = 15;
        this.baseAmount = amount;
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        baseAmount += stackAmount;
    }

    @Override
    public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && info.type == DamageInfo.DamageType.NORMAL) {
            drewBlood = true;
        }
    }

    @Override
    public void duringTurn() {
        addToBot(new DoAction(() -> {
            if (drewBlood) {
                amount = 1;
            } else {
                amount++;
            }
            drewBlood = false;
            flash();
            updateDescription();
        }));
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1] + baseAmount + DESCRIPTIONS[2];
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        Color origColor = sb.getColor();
        sb.setColor(new Color(1f, 1f - 0.05f * amount, 1f - 0.05f * amount, 1f));
        render(sb, tex);
        sb.setColor(origColor);
    }
}
