package ChimeraMonsters.curatedFights;

import ChimeraMonsters.curatedFights.CuratedFightsChainAPI.AbstractMonsterContext;
import ChimeraMonsters.curatedFights.CuratedFightsChainAPI.MonsterSetup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractReplacingCuratedFight extends AbstractCuratedFight {


    public AbstractReplacingCuratedFight(String id, Function<MonsterGroup, Boolean> isMonsterGroupValid, MonsterSetup monsterSetup){
        super(id, new AbstractMonsterContext(){
            @Override
            public Supplier<Boolean> contextCheck() {
                return ( () -> isMonsterGroupValid.apply(AbstractDungeon.getMonsters()));
            }
        }, monsterSetup);
    }
}
