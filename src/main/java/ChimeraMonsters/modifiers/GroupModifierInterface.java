package ChimeraMonsters.modifiers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public interface GroupModifierInterface {
    public boolean isMonsterGroupValid(MonsterGroup monsterGroup);

    public String fightName(MonsterGroup context);
}
