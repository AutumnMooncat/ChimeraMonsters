package ChimeraMonsters.util;

import ChimeraMonsters.cards.DummyCard;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.blights.Spear;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class CalcHelper {
    private static final DummyCard dummyCard = new DummyCard();

    public static int applyPowers(int damage) {
        dummyCard.setMultiDamage(false);
        dummyCard.baseDamage = damage;
        dummyCard.applyPowers();
        return dummyCard.damage;
    }

    public static int[] applyPowersMulti(int damage) {
        dummyCard.setMultiDamage(true);
        dummyCard.baseDamage = damage;
        dummyCard.applyPowers();
        return dummyCard.multiDamage;
    }

    public static int applyPowersToBlock(int block) {
        dummyCard.baseBlock = block;
        dummyCard.applyPowers();
        return dummyCard.block;
    }

    public static int calculateCardDamage(int damage, AbstractMonster mo) {
        dummyCard.setMultiDamage(false);
        dummyCard.baseDamage = damage;
        dummyCard.calculateCardDamage(mo);
        return dummyCard.damage;
    }

    public static int[] calculateCardDamageMulti(int damage) {
        dummyCard.setMultiDamage(true);
        dummyCard.baseDamage = damage;
        dummyCard.calculateCardDamage(null);
        return dummyCard.multiDamage;
    }

    public static int monsterToMonsterDamage(AbstractCreature source, AbstractCreature target, int dmg) {
        float tmp = (float)dmg;
        if (Settings.isEndless && AbstractDungeon.player.hasBlight(Spear.ID)) {
            float mod = AbstractDungeon.player.getBlight(Spear.ID).effectFloat();
            tmp *= mod;
        }

        for (AbstractPower power : source.powers) {
            tmp = power.atDamageGive(tmp, DamageInfo.DamageType.NORMAL);
        }
        for (AbstractPower power : target.powers) {
            tmp = power.atDamageReceive(tmp, DamageInfo.DamageType.NORMAL);
        }
        for (AbstractPower power : source.powers) {
            tmp = power.atDamageFinalGive(tmp, DamageInfo.DamageType.NORMAL);
        }
        for (AbstractPower power : target.powers) {
            tmp = power.atDamageFinalReceive(tmp, DamageInfo.DamageType.NORMAL);
        }

        dmg = MathUtils.floor(tmp);
        if (dmg < 0) {
            dmg = 0;
        }

        return dmg;
    }
}
