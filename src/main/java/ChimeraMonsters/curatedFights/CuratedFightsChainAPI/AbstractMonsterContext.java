package ChimeraMonsters.curatedFights.CuratedFightsChainAPI;

import java.util.function.Supplier;

public abstract class AbstractMonsterContext {
    public abstract Supplier<Boolean> contextCheck();
}
