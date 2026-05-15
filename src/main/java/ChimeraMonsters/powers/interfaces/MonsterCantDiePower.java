package ChimeraMonsters.powers.interfaces;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface MonsterCantDiePower {
    boolean cantDie(AbstractMonster monsterOwner);
    void onPreventDeath(AbstractMonster monsterOwner);
}
