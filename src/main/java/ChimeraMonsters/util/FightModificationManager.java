package ChimeraMonsters.util;

import ChimeraMonsters.ChimeraMonstersConfig;
import ChimeraMonsters.ChimeraMonstersController;
import ChimeraMonsters.modifiers.Modifier;
import ChimeraMonsters.modifiers.groups.addon.AbstractAddonModifier;
import ChimeraMonsters.modifiers.groups.curated.AbstractCuratedModifier;
import ChimeraMonsters.modifiers.groups.themed.AbstractThemedModifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.patches.MonsterFields;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FightModificationManager {

    public static String fightName = "";

    public static void rollFightModifiers(MonsterGroup monsterGroup) {
        List<AbstractCuratedModifier> validCurated = ChimeraMonstersController.getValidCuratedMods(monsterGroup).collect(Collectors.toList());
        List<AbstractThemedModifier> validThemed = ChimeraMonstersController.getValidThemedMods(monsterGroup).collect(Collectors.toList());
        int curated = validCurated.isEmpty() ? 0 : ChimeraMonstersConfig.IntSetting.CURATED_WEIGHT.getVal();
        int thematic = validThemed.isEmpty() ? 0 : ChimeraMonstersConfig.IntSetting.THEMED_WEIGHT.getVal();
        int modified = ChimeraMonstersConfig.IntSetting.ENHANCED_WEIGHT.getVal();
        int unmodified = ChimeraMonstersConfig.IntSetting.VANILLA_WEIGHT.getVal();
        if (curated + thematic + modified == 0) {
            fightName = "";
            return;
        }
        int roll = AbstractDungeon.miscRng.random(curated + thematic + modified + unmodified - 1); //StS adds +1 to random call, so subtract 1
        if ((roll -= curated) < 0) {
            AbstractCuratedModifier curatedMod = getWeighted(validCurated, getStrongestEnemyType(monsterGroup));
            if (curatedMod == null) {
                return;
            }
            fightName = curatedMod.getModifierName();
            curatedMod.applyTo(monsterGroup);
            if (ChimeraMonstersConfig.BoolSetting.ENHANCE_IN_CURATED.getVal()) {
                rollRandomModifiers(monsterGroup);
            }
            if (ChimeraMonstersConfig.BoolSetting.ADDON_IN_CURATED.getVal()) {
                rollAddon(monsterGroup);
            }
        } else if ((roll -= thematic) < 0) {
            AbstractThemedModifier themedMod = getWeighted(validThemed, getStrongestEnemyType(monsterGroup));
            if (themedMod == null) {
                return;
            }
            fightName = themedMod.getModifierName();
            themedMod.applyTo(monsterGroup);
            if (ChimeraMonstersConfig.BoolSetting.ENHANCE_IN_THEMED.getVal()) {
                rollRandomModifiers(monsterGroup);
            }
            if (ChimeraMonstersConfig.BoolSetting.ADDON_IN_THEMED.getVal()) {
                rollAddon(monsterGroup);
            }
        } else if ((roll -= modified) < 0) {
            fightName = "";
            rollRandomModifiers(monsterGroup);
            rollAddon(monsterGroup);
        }
    }

    public static void rollAddon(MonsterGroup monsterGroup) {
        if (ChimeraMonstersConfig.modEnabled() && ChimeraMonstersConfig.canRollWeights()) {
            if (AbstractDungeon.miscRng.random(99) < ChimeraMonstersConfig.IntSetting.ADDON_CHANCE.getVal()) {
                AbstractAddonModifier mod = getWeighted(ChimeraMonstersController.getValidAddonMods(monsterGroup), getStrongestEnemyType(monsterGroup));
                if (mod != null) {
                    mod.applyTo(monsterGroup);
                }
            }
        }
    }

    public static void rollRandomModifiers(MonsterGroup monsterGroup) {
        for (AbstractMonster m : monsterGroup.monsters) {
            rollMonsterModifier(m, monsterGroup);
        }
    }

    public static void rollMonsterModifier(AbstractMonster monster, MonsterGroup context) {
        if (ChimeraMonstersConfig.modEnabled() && !MonsterFields.rolledModifiers.get(monster) && ChimeraMonstersConfig.canRollWeights()) {
            for (int i = 0; i < ChimeraMonstersConfig.IntSetting.ROLL_ATTEMPTS.getVal() ; i++) {
                if (AbstractDungeon.miscRng.random(99) < ChimeraMonstersConfig.IntSetting.MOD_CHANCE.getVal()) {
                    AbstractMonsterModifier mod = getWeighted(ChimeraMonstersController.getValidMonsterMods(monster, context), monster.type);
                    if (mod != null) {
                        ChimeraMonstersController.applyModifier(monster, mod);
                    }
                }
            }
        }
        MonsterFields.rolledModifiers.set(monster, true);
    }

    public static Modifier.ModifierRarity rollRarity(List<? extends Modifier<?>> validMods, AbstractMonster.EnemyType type) {
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
        c = validMods.stream().anyMatch(m -> m.getModRarity() == Modifier.ModifierRarity.COMMON) ? c : 0;
        u = validMods.stream().anyMatch(m -> m.getModRarity() == Modifier.ModifierRarity.UNCOMMON) ? u : 0;
        r = validMods.stream().anyMatch(m -> m.getModRarity() == Modifier.ModifierRarity.RARE) ? r : 0;
        int roll = AbstractDungeon.miscRng.random(c + u + r - 1); //StS adds +1 to random call, so subtract 1
        if ((roll -= c) < 0) {
            return Modifier.ModifierRarity.COMMON;
        } else if (roll - u < 0) {
            return Modifier.ModifierRarity.UNCOMMON;
        } else {
            return Modifier.ModifierRarity.RARE;
        }
    }

    public static AbstractMonster.EnemyType getStrongestEnemyType(MonsterGroup group) {
        AbstractMonster.EnemyType type = AbstractMonster.EnemyType.NORMAL;
        if (group.monsters.stream().anyMatch(mon -> mon.type == AbstractMonster.EnemyType.BOSS)) {
            type = AbstractMonster.EnemyType.BOSS;
        } else if (group.monsters.stream().anyMatch(mon -> mon.type == AbstractMonster.EnemyType.ELITE)) {
            type = AbstractMonster.EnemyType.ELITE;
        }
        return type;
    }

    public static <T extends Modifier<?>> T getWeighted(Stream<T> mods, AbstractMonster.EnemyType type) {
        return getWeighted(mods.collect(Collectors.toList()), type);
    }

    public static <T extends Modifier<?>> T getWeighted(List<T> mods, AbstractMonster.EnemyType type) {
        if (!mods.isEmpty()) {
            Modifier.ModifierRarity rarity = rollRarity(mods, type);
            mods.removeIf(m -> m.getModRarity() != rarity);
            return mods.get(AbstractDungeon.miscRng.random(mods.size()-1));
        }
        return null;
    }
}
