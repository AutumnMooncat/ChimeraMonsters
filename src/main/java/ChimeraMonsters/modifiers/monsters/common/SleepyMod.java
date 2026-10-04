package ChimeraMonsters.modifiers.monsters.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.SleepyPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class SleepyMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(SleepyMod.class.getSimpleName());

    public SleepyMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noEliteOrBossCheck(monster, context);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new SleepyPower(monster, AbstractDungeon.actNum * 8));
    }


    @Override
    public AbstractMonsterModifier makeCopy() {
        return new SleepyMod();
    }
}
