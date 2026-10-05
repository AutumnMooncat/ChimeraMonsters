package ChimeraMonsters.modifiers.monsters.special;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.HexacursedPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class HexacurseMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(HexacurseMod.class.getSimpleName());

    public HexacurseMod() {
        super(ID, ModifierRarity.SPECIAL);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return monster.type != AbstractMonster.EnemyType.BOSS && actAtLeast(2);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseHealth(monster, DEBUFF_20);
        int base = AbstractDungeon.actNum + (monster.type == AbstractMonster.EnemyType.ELITE ? 1 : 0);
        applyPowersToCreature(monster, new HexacursedPower(monster, scaleDeadlier(monster, base, base + 1)));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new HexacurseMod();
    }

    @Override
    public String modifyName(AbstractMonster monster) {
        String[] nameWords = monster.name.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nameWords.length-1; i++) {
            sb.append(nameWords[i]);
            sb.append(" ");
        }
        sb.append(getPrefix());
        sb.append(nameWords[nameWords.length-1].toLowerCase());
        sb.append(getSuffix());
        return sb.toString();
    }
}
