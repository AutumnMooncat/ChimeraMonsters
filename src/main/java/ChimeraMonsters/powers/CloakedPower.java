package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.IntentHidingPower;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.orbs.Lightning;

public class CloakedPower extends AbstractEasyPower implements RenderModifierPower, IntentHidingPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(CloakedPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean hidden = true;

    public CloakedPower(AbstractCreature owner) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void atEndOfRound() {
        hidden = true;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (hidden && info.type == DamageInfo.DamageType.NORMAL && info.owner != null && info.owner != owner && damageAmount > 0) {
            flash();
            hidden = false;
        }
        return damageAmount;
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        if (hidden) {
            Color origColor = sb.getColor();
            sb.setColor(new Color(1, 1f, 1f, 0.75f));
            render(sb, tex);
            sb.setColor(origColor);
        } else {
            render(sb, tex);
        }
    }

    @Override
    public boolean shouldHide() {
        return hidden;
    }
}
