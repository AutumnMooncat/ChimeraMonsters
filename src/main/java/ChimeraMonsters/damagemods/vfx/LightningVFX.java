package ChimeraMonsters.damagemods.vfx;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;

import static ChimeraMonsters.ChimeraMonstersMod.makeID;

public class LightningVFX extends AbstractDamageModifier {
    public final static String ID = makeID(LightningVFX.class.getSimpleName());

    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target) {
        AbstractDungeon.effectList.add(new LightningEffect(target.hb.cX, target.hb.cY));
        CardCrawlGame.sound.play("THUNDERCLAP", 0.2f);
    }

    @Override
    public boolean isInherent() {
        return true;
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new LightningVFX();
    }
}
