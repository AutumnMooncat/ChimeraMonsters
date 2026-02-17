package ChimeraMonsters.curatedFights;

import ChimeraMonsters.curatedFights.CuratedFightsChainAPI.AbstractMonsterContext;
import ChimeraMonsters.curatedFights.CuratedFightsChainAPI.MonsterSetup;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public abstract class AbstractCuratedFight {

    public String id;

    public abstract String fightName(MonsterGroup context);

    public AbstractMonsterContext context;
    public MonsterSetup setup;

    public AbstractCuratedFight(String id, AbstractMonsterContext context, MonsterSetup setup){
        this.id = id;
        this.context = context;
        this.setup = setup;
    }
}
