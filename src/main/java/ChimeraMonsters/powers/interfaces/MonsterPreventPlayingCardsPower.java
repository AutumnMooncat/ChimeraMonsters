package ChimeraMonsters.powers.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface MonsterPreventPlayingCardsPower {
    boolean preventPlaying(AbstractCard card);
    boolean preventUsing(AbstractCard card, AbstractMonster m);
}
