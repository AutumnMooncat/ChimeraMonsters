package ChimeraMonsters.modifiers.monsters.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.BulkyPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class BulkyMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(BulkyMod.class.getSimpleName());

    public BulkyMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noEliteOrBossCheck(monster, context) && checkContext(context, noScaling);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseDamage(monster, DEBUFF_20);
        int block = scaleAbilities(monster, 3, 4) * (AbstractDungeon.actNum + 1);
        int str = scaleDeadlier(monster, 3, 4);
        str = scaleAbilities(monster, str, str + 1);
        applyPowersToCreature(monster, new BulkyPower(monster, block, str));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new BulkyMod();
    }
}
