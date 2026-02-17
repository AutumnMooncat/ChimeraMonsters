package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.MonsterPreventPlayingCardsPower;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.SpeechBubble;

public class TauntPower extends AbstractInternalLogicPower implements RenderModifierPower, MonsterPreventPlayingCardsPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(TauntPower.class.getSimpleName());
    private static final UIStrings cantUseText = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID("CantUseText"));
    private static final UIStrings speechText = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID("SpeechText"));
    private final float speechCooldown = 4f;
    private float remainingSpeechCooldown = speechCooldown;

    public TauntPower(AbstractCreature owner, int amount) {
        super(POWER_ID, owner, amount);

    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        Color origColor = sb.getColor();
        sb.setColor(new Color(1f, 0.7f, 0.75f, 1f));
        render(sb, tex, 0, 0, 1.2f, 0);
        this.remainingSpeechCooldown -= Gdx.graphics.getDeltaTime();
        if (this.remainingSpeechCooldown < 0.0F) {
            AbstractDungeon.effectList.add(new SpeechBubble(owner.hb.cX + (-50.0F * Settings.scale), owner.hb.cY + (70.0F * Settings.scale), 2.0F, speechText.TEXT[0], false));
            remainingSpeechCooldown = speechCooldown;
        }
        sb.setColor(origColor);
    }


    @Override
    public boolean preventPlaying(AbstractCard card) {
        return false;
    }

    @Override
    public boolean preventUsing(AbstractCard card, AbstractMonster m) {
        if (m == null || m.hasPower(TauntPower.POWER_ID)) {
            card.cantUseMessage = null;
            return false;
        } else {
            card.cantUseMessage = cantUseText.TEXT[1];
            return true;
        }

    }
}
