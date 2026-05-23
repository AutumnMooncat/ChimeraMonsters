package ChimeraMonsters.util;

import ChimeraMonsters.ChimeraMonstersConfig;
import ChimeraMonsters.ChimeraMonstersController;
import ChimeraMonsters.modifiers.AbstractModifier;
import ChimeraMonsters.modifiers.groups.curated.AbstractCuratedModifier;
import ChimeraMonsters.modifiers.groups.themed.AbstractThemedModifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
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
        List<AbstractCuratedModifier> validCurated = new ArrayList<>();
        List<AbstractThemedModifier> validThemed = new ArrayList<>();
        int curated = validCurated.isEmpty() ? 0 : ChimeraMonstersConfig.IntSetting.CURATED_WEIGHT.getVal();
        int thematic = validThemed.isEmpty() ? 0 : ChimeraMonstersConfig.IntSetting.THEMED_WEIGHT.getVal();
        int modified = ChimeraMonstersConfig.IntSetting.ENHANCED_WEIGHT.getVal();
        int unmodified = ChimeraMonstersConfig.IntSetting.VANILLA_WEIGHT.getVal();
        int roll = AbstractDungeon.miscRng.random(curated + thematic + modified + unmodified - 1); //StS adds +1 to random call, so subtract 1
        if ((roll -= curated) < 0 && !validCurated.isEmpty()) {
            AbstractCuratedModifier curatedFight = validCurated.get(AbstractDungeon.miscRng.random(validCurated.size() - 1));
            curatedFight.applyTo(monsterGroup);
            fightName = curatedFight.getModifierName();
        } else if ((roll -= thematic) < 0 && !validThemed.isEmpty()) {
            AbstractThemedModifier themedModifier = validThemed.get(AbstractDungeon.miscRng.random(validThemed.size() - 1));
            themedModifier.applyTo(monsterGroup);
            fightName = themedModifier.getModifierName();
        } else if ((roll -= modified) < 0) {
            rollRandomModifiers(monsterGroup);
            fightName = "";
        }
    }

    private static void rollRandomModifiers(MonsterGroup monsterGroup){
        for (AbstractMonster m : monsterGroup.monsters) {
            rollMonsterModifier(m, monsterGroup);
        }
    }

    public static void rollMonsterModifier(AbstractMonster monster, MonsterGroup context) {
        if (ChimeraMonstersConfig.BoolSetting.ENABLE_MOD.getVal() && !MonsterFields.rolledModifiers.get(monster) && (ChimeraMonstersConfig.IntSetting.COMMON_WEIGHT.getVal() + ChimeraMonstersConfig.IntSetting.UNCOMMON_WEIGHT.getVal() + ChimeraMonstersConfig.IntSetting.RARE_WEIGHT.getVal() + ChimeraMonstersConfig.IntSetting.RARITY_BIAS.getVal() != 0)) {
            for (int i = 0; i < ChimeraMonstersConfig.IntSetting.ROLL_ATTEMPTS.getVal() ; i++) {
                if (AbstractDungeon.miscRng.random(99) < ChimeraMonstersConfig.IntSetting.MOD_CHANCE.getVal()) {
                    applyWeightedMonsterModifier(monster, context, rollRarity(monster.type));
                }
            }
        }
        MonsterFields.rolledModifiers.set(monster, true);
    }

    public static AbstractModifier.ModifierRarity rollRarity(AbstractMonster.EnemyType type) {
        int c = ChimeraMonstersConfig.IntSetting.COMMON_WEIGHT.getVal();
        int u = ChimeraMonstersConfig.IntSetting.UNCOMMON_WEIGHT.getVal();
        int r = ChimeraMonstersConfig.IntSetting.RARE_WEIGHT.getVal();
        int b = ChimeraMonstersConfig.IntSetting.RARITY_BIAS.getVal();
        if (type != null) {
            switch (type) {
                case NORMAL:
                    c += b;
                    break;
                case ELITE:
                    u += b;
                    break;
                case BOSS:
                    r += b;
                    break;
            }
        }
        int roll = AbstractDungeon.miscRng.random(c + u + r - 1); //StS adds +1 to random call, so subtract 1
        if ((roll -= c) < 0) {
            return AbstractModifier.ModifierRarity.COMMON;
        } else if (roll - u < 0) {
            return AbstractModifier.ModifierRarity.UNCOMMON;
        } else {
            return AbstractModifier.ModifierRarity.RARE;
        }
    }

    public static void applyWeightedMonsterModifier(AbstractMonster monster, MonsterGroup context, AbstractModifier.ModifierRarity rarity) {
        List<AbstractMonsterModifier> validMods = ChimeraMonstersController.getValidMonsterModsOfRarity(monster, context, rarity).collect(Collectors.toList());
        if (!validMods.isEmpty()) {
            AbstractMonsterModifier mod = validMods.get(AbstractDungeon.miscRng.random(validMods.size()-1)).makeCopy();
            ChimeraMonstersController.applyModifier(monster, mod);
        }
    }
}
