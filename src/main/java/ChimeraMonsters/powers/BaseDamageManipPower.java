package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class BaseDamageManipPower extends AbstractInternalLogicPower implements NonStackablePower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(BaseDamageManipPower.class.getSimpleName());
    public final float multi;

    public BaseDamageManipPower(AbstractCreature owner, float multi) {
        super(POWER_ID, owner, -1);
        this.priority = -999999;
        this.multi = multi;
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return (float) ((int) (damage * multi));
        }
        return damage;
    }
}
