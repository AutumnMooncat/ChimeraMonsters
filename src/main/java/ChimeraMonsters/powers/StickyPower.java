package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class StickyPower extends AbstractEasyPower implements RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(StickyPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private float timePassed;

    public StickyPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void updateDescription() {
        if (amount == 1) {
            description = DESCRIPTIONS[0];
        } else {
            description = DESCRIPTIONS[1] + amount + DESCRIPTIONS[2];
        }
    }

    @Override
    public void onInflictDamage(DamageInfo info, int damageAmount, AbstractCreature target) {
        if (damageAmount > 0 && info.type != DamageInfo.DamageType.THORNS) {
            addToBot(new MakeTempCardInDiscardAction(new Slimed(), amount));
        }
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        timePassed += Gdx.graphics.getDeltaTime();
        Color origColor = sb.getColor();
        sb.setColor(new Color(0.85f, 1f, 0.85f, 1f));
        render(sb, tex, 0f, 0f, (float) (0.95f+Math.sin(timePassed*2f)*0.05f), (float) (0.95f+Math.cos(timePassed*2f)*0.05f), 0f);
        sb.setColor(origColor);
    }
}
