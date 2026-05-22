package ChimeraMonsters.modifiers.monsters.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.BurningPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class BurningMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(BurningMod.class.getSimpleName());

    public BurningMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noEliteOrBossCheck(monster, context);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new BurningPower(monster, AbstractDungeon.actNum * 2));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new BurningMod();
    }
}
