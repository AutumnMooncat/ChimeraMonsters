package ChimeraMonsters.util;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.curatedFights.AbstractCuratedFight;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.modifiers.GroupMonsterModifier;
import ChimeraMonsters.patches.MonsterFields;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FightModificationManager {

    public static String fightName = "";

    public static void rollFightModifiers(MonsterGroup monsterGroup) {
        List<AbstractCuratedFight> validCurated = ChimeraMonstersMod.curatedFightMap.values().stream().filter(fight -> fight.isMonsterGroupValid(monsterGroup)).collect(Collectors.toList());
        List<GroupMonsterModifier> validThematic = ChimeraMonstersMod.modMap.values().stream().filter(modifier -> modifier instanceof GroupMonsterModifier && ((GroupMonsterModifier) modifier).isMonsterGroupValid(monsterGroup)).map(modifier -> ((GroupMonsterModifier) modifier)).collect(Collectors.toList());
        int curated = validCurated.isEmpty() ? 0 : ChimeraMonstersMod.curatedFightWeight;
        int thematic = validThematic.isEmpty() ? 0 : ChimeraMonstersMod.thematicFightWeight;
        int modified = ChimeraMonstersMod.modifiedFightWeight;
        int unmodified = ChimeraMonstersMod.unmodifiedFightWeight;
        int roll = AbstractDungeon.miscRng.random(curated + thematic + modified + unmodified - 1); //StS adds +1 to random call, so subtract 1
        if ((roll -= curated) < 0) {
            AbstractCuratedFight curatedFight = validCurated.get(AbstractDungeon.miscRng.random(validCurated.size() - 1));
            //TODO: do the thing
            fightName = curatedFight.fightName(monsterGroup);
        } else if ((roll -= thematic) < 0) {
            GroupMonsterModifier thematicModifier = validThematic.get(AbstractDungeon.miscRng.random(validThematic.size() - 1));
            fightName = thematicModifier.fightName(monsterGroup);
            for (AbstractMonster m : monsterGroup.monsters) {
                ChimeraMonstersMod.applyModifier(m, thematicModifier);
            }
        } else if ((roll -= modified) < 0) {
            rollRandomModifiers(monsterGroup);
        }
    }

    private static void rollRandomModifiers(MonsterGroup monsterGroup){
        for(AbstractMonster m : monsterGroup.monsters) {
           rollMonsterModifier(m, monsterGroup);
        }
        fightName="";
    }

    public static void rollMonsterModifier(AbstractMonster monster, MonsterGroup context) {
        if (ChimeraMonstersMod.enableMods && !MonsterFields.rolledModifiers.get(monster) && (ChimeraMonstersMod.commonWeight + ChimeraMonstersMod.uncommonWeight + ChimeraMonstersMod.rareWeight + ChimeraMonstersMod.rarityBias != 0)) {
            for (int i = 0 ; i < ChimeraMonstersMod.rollAttempts ; i++) {
                if (AbstractDungeon.miscRng.random(99) < ChimeraMonstersMod.modProbabilityPercent) {
                    applyWeightedMonsterModifier(monster, context, rollRarity(monster.type));
                }
            }
        }
        MonsterFields.rolledModifiers.set(monster, true);
    }

    public static AbstractMonsterModifier.ModifierRarity rollRarity(AbstractMonster.EnemyType type) {
        int c = ChimeraMonstersMod.commonWeight;
        int u = ChimeraMonstersMod.uncommonWeight;
        int r = ChimeraMonstersMod.rareWeight;
        switch (type) {
            case NORMAL:
                c += ChimeraMonstersMod.rarityBias;
                break;
            case ELITE:
                u += ChimeraMonstersMod.rarityBias;
                break;
            case BOSS:
                r += ChimeraMonstersMod.rarityBias;
                break;
        }
        int roll = AbstractDungeon.miscRng.random(c + u + r - 1); //StS adds +1 to random call, so subtract 1
        if ((roll -= c) < 0) {
            return AbstractMonsterModifier.ModifierRarity.COMMON;
        } else if (roll - u < 0) {
            return AbstractMonsterModifier.ModifierRarity.UNCOMMON;
        } else {
            return AbstractMonsterModifier.ModifierRarity.RARE;
        }
    }

    public static void applyWeightedMonsterModifier(AbstractMonster monster, MonsterGroup context, AbstractMonsterModifier.ModifierRarity rarity) {
        ArrayList<AbstractMonsterModifier> validMods = new ArrayList<>();
        switch (rarity) {
            case COMMON:
                validMods.addAll(ChimeraMonstersMod.commonMods.stream().filter(m -> m.canApplyTo(monster, context) && ChimeraMonstersMod.isModifierEnabled(m)).collect(Collectors.toCollection(ArrayList::new)));
                break;
            case UNCOMMON:
                validMods.addAll(ChimeraMonstersMod.uncommonMods.stream().filter(m -> m.canApplyTo(monster, context) && ChimeraMonstersMod.isModifierEnabled(m)).collect(Collectors.toCollection(ArrayList::new)));
                break;
            case RARE:
                validMods.addAll(ChimeraMonstersMod.rareMods.stream().filter(m -> m.canApplyTo(monster, context) && ChimeraMonstersMod.isModifierEnabled(m)).collect(Collectors.toCollection(ArrayList::new)));
                break;
        }
        if (!validMods.isEmpty()) {
            AbstractMonsterModifier mod = validMods.get(AbstractDungeon.miscRng.random(validMods.size()-1)).makeCopy();
            ChimeraMonstersMod.applyModifier(monster, mod);
        }
    }
}
