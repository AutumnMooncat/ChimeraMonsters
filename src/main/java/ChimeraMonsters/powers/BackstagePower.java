package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.MonsterPreventPlayingCardsPower;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.ShaderCompiler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BackstagePower extends AbstractInternalLogicPower implements RenderModifierPower, MonsterPreventPlayingCardsPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(BackstagePower.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID("CantUseText"));

    public BackstagePower(AbstractCreature owner, int amount) {
        super(POWER_ID, owner, amount);
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        Color origColor = sb.getColor();
        //sb.setColor(Color.WHITE);
        if(!isLast()){
            sb.setColor(new Color(0.75f,0.7f,0.75f,1f));
            render(sb, tex, 0, 150, 0.6f, 10);
        } else {
            sb.setColor(Color.WHITE);
            render(sb, tex);
        }

        sb.setColor(origColor);
    }

    public boolean isLast(){
        for (AbstractMonster mon : AbstractDungeon.getMonsters().monsters){
            if(!mon.isDeadOrEscaped()&&!mon.equals(owner)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean preventPlaying(AbstractCard card) {
        return false;
    }

    @Override
    public boolean preventUsing(AbstractCard card, AbstractMonster m) {
        if(m==null || (m!=null && !m.hasPower(BackstagePower.POWER_ID))){
            card.cantUseMessage = null;
            return false;
        }
        if(isLast()){
            card.cantUseMessage = null;
            return false;
        } else {
            card.cantUseMessage = uiStrings.TEXT[0];
            return true;
        }

    }
}
