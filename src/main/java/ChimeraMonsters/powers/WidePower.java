package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.CreatureRenderPatches;
import ChimeraMonsters.powers.interfaces.IntentInterceptingPower;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class WidePower extends AbstractInternalLogicPower implements RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(WidePower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final float SCALE = 1.33f;

    public WidePower(AbstractCreature owner) {
        super(POWER_ID, owner, -1);
        owner.hb_w *= SCALE;
        owner.hb.width *= SCALE;
        owner.healthBarUpdatedEvent();
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        render(sb, tex, 0f, 0f, SCALE, 1f,1f);
    }

    @Override
    public float animationRate() {
        return 0.85f;
    }
}
