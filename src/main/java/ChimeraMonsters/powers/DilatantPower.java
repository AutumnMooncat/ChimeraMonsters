package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class DilatantPower extends AbstractEasyPower implements RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(DilatantPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private float hitScale = 1f;
    private float timePassed;

    public DilatantPower(AbstractCreature owner) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null) {
            flash();
            // Malleable adds to bottom, do the same to meet player expectation?
            hitScale += (damageAmount / 2f) / owner.maxHealth;
            addToBot(new GainBlockAction(owner, owner, Math.max(1, damageAmount/2)));
        }
        return damageAmount;
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        timePassed += Gdx.graphics.getDeltaTime();
        if (hitScale > 1f) {
            hitScale = Math.max(1f, hitScale - hitScale * Gdx.graphics.getDeltaTime());
        }
        render(sb, tex, 0f, 0f, (float) (hitScale + Math.sin(timePassed)/10f), (float) (hitScale + Math.cos(timePassed)/10f), 0f);
    }
}
