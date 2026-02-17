package ChimeraMonsters.curatedFights;

import ChimeraMonsters.curatedFights.CuratedFightsChainAPI.AbstractMonsterContext;
import ChimeraMonsters.curatedFights.CuratedFightsChainAPI.MonsterSetup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.function.Supplier;

public abstract class AbstractExtraCuratedFight extends AbstractCuratedFight{
    public AbstractExtraCuratedFight(String id, MonsterSetup monsterSetup, Class roomType, int act){
        super(id, new AbstractMonsterContext(){
            @Override
            public Supplier<Boolean> contextCheck() {
                return ( () -> AbstractDungeon.getCurrRoom().getClass().equals(roomType)&&AbstractDungeon.actNum==act);
            }
        }, monsterSetup);
    }
}
