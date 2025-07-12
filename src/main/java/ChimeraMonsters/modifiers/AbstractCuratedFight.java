package ChimeraMonsters.modifiers;

import com.megacrit.cardcrawl.monsters.MonsterGroup;

public abstract class AbstractCuratedFight {

    public abstract boolean isMonsterGroupValid(MonsterGroup monsterGroup);

    public abstract String fightName(MonsterGroup context);
}
