package ChimeraMonsters.util;

import ChimeraMonsters.ChimeraMonstersMod;
import basemod.BaseMod;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import java.util.ArrayList;

public class MonsterSpawnHelper {
    public static ArrayList<AbstractMonster> getSplitMonsters(AbstractMonster original, String encounterID){
        ArrayList<AbstractMonster> splitMonsters = new ArrayList<>();
        int tries=0;
        do {
            MonsterGroup group = BaseMod.getMonster(encounterID);
            if(group==null){
                group = MonsterHelper.getEncounter(encounterID);
            }

            tries++;
            for (AbstractMonster monster : group.monsters) {
                if (monster == null) {
                    ChimeraMonstersMod.logger.warn("Got null monster from {} -> {}", original.name, encounterID);
                    continue;
                }
                if (original.getClass().equals(monster.getClass())) {
                    monster.maxHealth=original.currentHealth;
                    monster.currentHealth=monster.maxHealth;
                    monster.drawX= (float) (original.drawX+(Math.pow(-1,(splitMonsters.size()%2+1))*100f*Settings.xScale));
                    monster.drawY=original.drawY;
                    splitMonsters.add(monster);
                }
            }
        } while (splitMonsters.size()<2 && tries < 100);
        if(splitMonsters.size()<2){
            ChimeraMonstersMod.logger.warn("Could not find split monsters");
        }
        return splitMonsters;
    }
}
