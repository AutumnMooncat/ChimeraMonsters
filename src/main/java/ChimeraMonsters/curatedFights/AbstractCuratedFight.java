package ChimeraMonsters.curatedFights;

import com.megacrit.cardcrawl.monsters.MonsterGroup;

public abstract class AbstractCuratedFight {

    public abstract String identifier();

    public abstract boolean isMonsterGroupValid(MonsterGroup monsterGroup);

    public abstract String fightName(MonsterGroup context);
}
