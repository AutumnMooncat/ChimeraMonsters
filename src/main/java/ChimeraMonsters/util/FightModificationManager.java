package ChimeraMonsters.util;

import ChimeraMonsters.modifiers.AbstractCuratedFight;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FightModificationManager {
    ArrayList<AbstractMonsterModifier> thematicModifiers = new ArrayList<>();
    ArrayList<AbstractCuratedFight> curatedFights = new ArrayList<>();


    public void rollFightModifiers (MonsterGroup monsterGroup){
        int thematicWeight = 25; //TODO: calculate
        //TODO: Seeded RNG
        int randomNumber = (int)(Math.random()*100);
        if (randomNumber<25){
            List<AbstractCuratedFight> validCuratedFights = curatedFights.stream().filter(fight -> fight.isMonsterGroupValid(monsterGroup)).collect(Collectors.toList());
            //if not empty choose one, apply modifiers and fight name
            //if empty go fully random
        } else if (randomNumber<25+thematicWeight){
            //choose a valid thematic fight , apply modifiers and  fight name
            //if empty go fully random
        } else {
            //go fully random
        }

    }
}
