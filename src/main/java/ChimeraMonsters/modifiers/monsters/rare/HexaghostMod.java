package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.HexaghostPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class HexaghostMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(HexaghostMod.class.getSimpleName());

    public HexaghostMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return monster.type != AbstractMonster.EnemyType.BOSS && actAtLeast(2);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseHealth(monster, DEBUFF_20);
        int base = AbstractDungeon.actNum + (monster.type == AbstractMonster.EnemyType.ELITE ? 1 : 0);
        applyPowersToCreature(monster, new HexaghostPower(monster, scaleDeadlier(monster, base, base + 1)));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new HexaghostMod();
    }
}
