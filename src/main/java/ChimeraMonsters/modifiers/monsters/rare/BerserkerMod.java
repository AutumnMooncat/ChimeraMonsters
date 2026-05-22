package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.BerserkerPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class BerserkerMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(BerserkerMod.class.getSimpleName());

    public BerserkerMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return checkContext(context, multiCombat) && checkContext(context, monster, lastMonster) && monster.type == AbstractMonster.EnemyType.NORMAL;
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new BerserkerPower(monster, 3));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new BerserkerMod();
    }
}
