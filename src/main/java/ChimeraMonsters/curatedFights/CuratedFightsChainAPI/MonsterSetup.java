package ChimeraMonsters.curatedFights.CuratedFightsChainAPI;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class MonsterSetup {
    public ArrayList<AbstractMonster> monsters;
    private MonsterSetup() {

    }

    public MonsterSetupMonster addMonster(AbstractMonster monster){
        this.monsters.add(monster);
        return new MonsterSetupMonster(this);
    }
}
