package ChimeraMonsters.util;

import ChimeraMonsters.patches.MonsterFields;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.Collections;

public interface MonsterManipulator {
    float BUFF_50 = 3 / 2f;
    float BUFF_33 = 4 / 3f;
    float BUFF_25 = 5 / 4f;
    float BUFF_20 = 6 / 5f;
    float BUFF_10 = 11 / 10f;
    float DEBUFF_50 = 1 / 2f;
    float DEBUFF_33 = 2 / 3f;
    float DEBUFF_25 = 3 / 4f;
    float DEBUFF_20 = 4 / 5f;
    float DEBUFF_10 = 9 / 10f;

    default void applyPowersToCreature(AbstractCreature owner, AbstractPower... powers) {
        for (AbstractPower powerToApply : powers) {
            AbstractPower p = owner.getPower(powerToApply.ID);
            if (p != null) {
                p.stackPower(powerToApply.amount);
                if (!(p instanceof InvisiblePower)) {
                    p.flash();
                }
                p.updateDescription();
            } else {
                owner.addPower(powerToApply);
                Collections.sort(owner.powers);
                powerToApply.onInitialApplication();
                if (!(powerToApply instanceof InvisiblePower)) {
                    powerToApply.flash();
                }
            }
            //AbstractDungeon.onModifyPower();
        }
        //TODO: Apply Buff/Debuff VFX
    }

    default void manipulateBaseHealth(AbstractMonster monster, float factor) {
        monster.currentHealth = Math.max(1,(int) (monster.currentHealth * factor));
        monster.maxHealth = Math.max(1,(int) (monster.maxHealth * factor));
    }

    default void startDamaged(AbstractMonster monster, float factor){
        monster.currentHealth = Math.max(1, Math.min(monster.currentHealth,(int) (monster.maxHealth * factor)));
    }

    default void manipulateBaseDamage(AbstractMonster monster, float factor) {
        //TODO: what to do about Hexaghost Turn 2 and similar attacks
        for (DamageInfo di : monster.damage) {
            di.base = (int) (di.base * factor);
        }
    }

    default void manipulateFinalBlock(AbstractMonster monster, float factor) {
        MonsterFields.blockMulti.set(monster, MonsterFields.blockMulti.get(monster) * factor);
    }
}
