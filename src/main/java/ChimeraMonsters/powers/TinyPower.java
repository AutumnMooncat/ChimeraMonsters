package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.CreatureRenderPatches;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.IntentManager;
import ChimeraMonsters.util.IntentPayload;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TinyPower extends AbstractInternalLogicPower implements RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(TinyPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final float SCALE = 0.66f;
    private IntentPayload pl;

    public TinyPower(AbstractCreature owner) {
        super(POWER_ID, owner, -1);
        owner.hb_w *= SCALE;
        owner.hb_h *= SCALE;
        owner.hb.width *= SCALE;
        owner.hb.height *= SCALE;
        owner.healthBarUpdatedEvent();
    }

    @Override
    public void onInitialApplication() {
        if (owner instanceof AbstractMonster) {
            pl = new IntentPayload();
            pl.owner = (AbstractMonster) owner;
            IntentManager.addExtraIntent((AbstractMonster) owner, pl, true);
        }
    }

    @Override
    public void onRemove() {
        if (owner instanceof AbstractMonster) {
            IntentManager.removeExtraIntent((AbstractMonster) owner, pl);
        }
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        render(sb, tex, 0f, -CreatureRenderPatches.currentTransform()[4]*(1f-SCALE), SCALE, SCALE);
    }

    @Override
    public float animationRate() {
        return 1.5f;
    }
}
