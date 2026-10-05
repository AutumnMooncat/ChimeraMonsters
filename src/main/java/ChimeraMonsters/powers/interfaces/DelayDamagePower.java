package ChimeraMonsters.powers.interfaces;

import com.megacrit.cardcrawl.cards.DamageInfo;

public interface DelayDamagePower {
    boolean shouldDelay(DamageInfo info);
}
