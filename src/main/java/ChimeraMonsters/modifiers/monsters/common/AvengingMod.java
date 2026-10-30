package ChimeraMonsters.modifiers.monsters.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.AvengingPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class AvengingMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(AvengingMod.class.getSimpleName());

    public AvengingMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return checkContext(context, multiCombat) && noBossCheck(monster, context);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseDamage(monster, DEBUFF_25);
        int str = AbstractDungeon.actNum * scaleAbilities(monster, 5, 6);
        applyPowersToCreature(monster, new AvengingPower(monster, str));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new AvengingMod();
    }
}
