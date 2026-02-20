package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.MonsterPreventPlayingCardsPower;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class NormalPower extends AbstractInternalLogicPower implements RenderModifierPower, MonsterPreventPlayingCardsPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(NormalPower.class.getSimpleName());
    private static final UIStrings cantUseText = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID("CantUseText"));

    public NormalPower(AbstractCreature owner, int amount) {
        super(POWER_ID, owner, amount);
        priority=-200;

    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        Color origColor = sb.getColor();
        if (AbstractDungeon.actionManager.cardsPlayedThisTurn.size() >= 3) {
            sb.setColor(new Color(0.6f, 0.6f, 0.6f, 1f));
            render(sb, tex, 0, 0, 1.2f, 90);
        } else {
            sb.setColor(Color.WHITE);
            render(sb, tex);
        }
        sb.setColor(origColor);
    }


    @Override
    public boolean preventPlaying(AbstractCard card) {
        if (AbstractDungeon.actionManager.cardsPlayedThisTurn.size() >= 3) {
            card.cantUseMessage = cantUseText.TEXT[2];
            return true;
        }
        return false;
    }

    @Override
    public boolean preventUsing(AbstractCard card, AbstractMonster m) {
        return false;
    }
}
