package ChimeraMonsters.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;

public class GreedDamage extends AbstractDamageModifier {
    int gold;

    public GreedDamage(int gold) {
        this.priority = Short.MAX_VALUE;
        this.gold = gold;
    }

    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature targetHit) {
        if (info.owner != null && !info.owner.isPlayer && targetHit.isPlayer) {
            CardCrawlGame.sound.play("GOLD_JINGLE");
            int goldAmount = gold;
            if (targetHit.gold < goldAmount) {
                goldAmount = targetHit.gold;
            }

            targetHit.gold -= goldAmount;

            for(int i = 0; i < goldAmount; ++i) {
                AbstractDungeon.effectList.add(new GainPennyEffect(info.owner, targetHit.hb.cX, targetHit.hb.cY, info.owner.hb.cX, info.owner.hb.cY, false));
            }
        }
    }

    @Override
    public boolean isInherent() {
        return true;
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new GreedDamage(gold);
    }
}
