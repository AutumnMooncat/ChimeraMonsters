package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.ShaderCompiler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;

public class CrackedPower extends AbstractEasyPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(CrackedPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    /*private static final ShaderProgram sp = ShaderCompiler.makeFrag(ChimeraMonstersMod.makePath("shaders/cracked.frag"));
    private float time;*/

    public CrackedPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.DEBUFF, false, owner, amount);
    }

    @Override
    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("ORB_FROST_EVOKE", 0.1F);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount*10 + DESCRIPTIONS[1];
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType damageType) {
        if (damageType == DamageInfo.DamageType.NORMAL) {
            return damage * (1 + amount/10f);
        }
        return damage;
    }

    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        if (info.owner != null && info.owner != owner && info.type == DamageInfo.DamageType.NORMAL && damageAmount > 0) {
            flash();
            addToBot(new ApplyPowerAction(owner, owner, new CrackedPower(owner, 1)));
        }
    }

    /*@Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        Color origColor = sb.getColor();
        sb.setColor(Color.WHITE);
        render(sb, tex);
        time += Gdx.graphics.getDeltaTime();
        blitShader(sb, tex, sp, u -> {
            u.setUniformf("x_time", time);
        });
        sb.setColor(origColor);
    }*/
}
