package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;


public class HoverPower extends AbstractEasyPower implements RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(HoverPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private float timePassed;

    public HoverPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        timePassed=0f;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
        return calculateDamageTakenAmount(damage, type);
    }

    private float calculateDamageTakenAmount(float damage, DamageInfo.DamageType type) {
        if (type != DamageInfo.DamageType.HP_LOSS && type != DamageInfo.DamageType.THORNS)
            return damage / 2.0F;
        return damage;
    }

    public int onAttacked(DamageInfo info, int damageAmount) {
        boolean willLive = (calculateDamageTakenAmount(damageAmount, info.type) < this.owner.currentHealth);
        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0 && willLive) {
            flash();
            addToBot(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
        }
        return damageAmount;
    }

    public void onRemove() {
        addToBot(new LoseHPAction(owner, owner, (int) (0.25f * this.owner.maxHealth)));
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        timePassed+=Gdx.graphics.getDeltaTime();
        Color origColor = sb.getColor();
        sb.setColor(Color.WHITE);
        render(sb, tex, (float) (50 * Math.sin(timePassed)), 150f + (float) (50 * Math.sin(timePassed)), 1f, 15);

        sb.setColor(origColor);
    }
}
