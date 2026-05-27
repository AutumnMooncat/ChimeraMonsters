package ChimeraMonsters;

import ChimeraMonsters.modifiers.Modifier;
import ChimeraMonsters.modifiers.groups.addon.AbstractAddonModifier;
import ChimeraMonsters.modifiers.groups.curated.AbstractCuratedModifier;
import ChimeraMonsters.modifiers.groups.themed.AbstractThemedModifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.patches.MonsterFields;
import ChimeraMonsters.powers.ModifierExplainerPower;
import ChimeraMonsters.ui.TopPanelExplainer;
import ChimeraMonsters.util.Wiz;
import ChimeraMonsters.util.compat.SpirePeopleCompat;
import basemod.patches.com.megacrit.cardcrawl.helpers.TopPanel.TopPanelHelper;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChimeraMonstersController {
    //Mod Lists
    public static final HashMap<Modifier.ModifierRarity, ArrayList<AbstractMonsterModifier>> monsterMods = new HashMap<>();
    public static final HashMap<Modifier.ModifierRarity, ArrayList<AbstractThemedModifier>> themedMods = new HashMap<>();
    public static final HashMap<Modifier.ModifierRarity, ArrayList<AbstractCuratedModifier>> curatedMods = new HashMap<>();
    public static final HashMap<Modifier.ModifierRarity, ArrayList<AbstractAddonModifier>> addonMods = new HashMap<>();
    public static final HashMap<String, Modifier<?>> modifierMap = new HashMap<>();

    public static TopPanelExplainer explainer;
    public static boolean explainerPresent;

    public static void applyModifier(AbstractMonster monster, AbstractMonsterModifier mod) {
        AbstractMonsterModifier copy = mod.makeCopy();
        if (CardCrawlGame.isInARun()) {
            copy.applyTo(monster);
            if (ChimeraMonstersConfig.BoolSetting.ENABLE_TOOLTIPS.getVal()) {
                monster.powers.add(new ModifierExplainerPower(monster, copy.getModifierName(), copy.getModifierDescription()));
            }
            if (!explainerPresent) {
                TopPanelHelper.topPanelGroup.addPanelItem(explainer);
                explainerPresent = true;
            }
        }
        monster.name = copy.modifyName(monster);
        if (Loader.isModLoaded("SpirePeople")) {
            SpirePeopleCompat.fixName(monster);
        }
        MonsterFields.receivedModifiers.get(monster).add(copy);
    }

    public static List<Modifier<?>> currentCombatModifiers() {
        List<Modifier<?>> mods = new ArrayList<>();
        if (Wiz.isInCombat()) {
            Wiz.forAllMonstersLiving(mon ->
                    mods.addAll(MonsterFields.receivedModifiers.get(mon)
                            .stream().filter(check ->
                                    mods.stream().noneMatch(mod ->
                                            mod.getClass() == check.getClass()))
                            .collect(Collectors.toList())));
        }
        return mods;
    }

    public static boolean canReceiveModifier(AbstractMonster monster, MonsterGroup context) {
        if (getValidMonsterModsOfRarity(monster, context, Modifier.ModifierRarity.COMMON).findAny().isPresent()) {
            return true;
        }
        if (getValidMonsterModsOfRarity(monster, context, Modifier.ModifierRarity.UNCOMMON).findAny().isPresent()) {
            return true;
        }
        if (getValidMonsterModsOfRarity(monster, context, Modifier.ModifierRarity.RARE).findAny().isPresent()) {
            return true;
        }
        return false;
    }

    public static boolean canReceiveModifier(MonsterGroup group) {
        if (getValidThemedModsOfRarity(group, Modifier.ModifierRarity.COMMON).findAny().isPresent()) {
            return true;
        }
        if (getValidThemedModsOfRarity(group, Modifier.ModifierRarity.UNCOMMON).findAny().isPresent()) {
            return true;
        }
        if (getValidThemedModsOfRarity(group, Modifier.ModifierRarity.RARE).findAny().isPresent()) {
            return true;
        }
        return false;
    }

    public static Stream<AbstractMonsterModifier> getMonsterMods() {
        return monsterMods.values().stream().flatMap(Collection::stream).filter(ChimeraMonstersConfig::isModifierEnabled);
    }

    public static Stream<AbstractMonsterModifier> getValidMonsterMods(AbstractMonster mon, MonsterGroup context) {
        return getMonsterMods().filter(mod -> mod.canApplyTo(mon, context));
    }

    public static Stream<AbstractMonsterModifier> getMonsterModsOfRarity(Modifier.ModifierRarity rarity) {
        return monsterMods.get(rarity).stream().filter(ChimeraMonstersConfig::isModifierEnabled);
    }

    public static Stream<AbstractMonsterModifier> getValidMonsterModsOfRarity(AbstractMonster mon, MonsterGroup context, Modifier.ModifierRarity rarity) {
        return getMonsterModsOfRarity(rarity).filter(mod -> mod.canApplyTo(mon, context));
    }

    public static Stream<AbstractThemedModifier> getThemedMods() {
        return themedMods.values().stream().flatMap(Collection::stream).filter(ChimeraMonstersConfig::isModifierEnabled);
    }

    public static Stream<AbstractThemedModifier> getValidThemedMods(MonsterGroup group) {
        return getThemedMods().filter(mod -> mod.canApplyTo(group));
    }

    public static Stream<AbstractThemedModifier> getThemedModsOfRarity(Modifier.ModifierRarity rarity) {
        return themedMods.get(rarity).stream().filter(ChimeraMonstersConfig::isModifierEnabled);
    }

    public static Stream<AbstractThemedModifier> getValidThemedModsOfRarity(MonsterGroup group, Modifier.ModifierRarity rarity) {
        return getThemedModsOfRarity(rarity).filter(mod -> mod.canApplyTo(group));
    }

    public static Stream<AbstractCuratedModifier> getCuratedMods() {
        return curatedMods.values().stream().flatMap(Collection::stream).filter(ChimeraMonstersConfig::isModifierEnabled);
    }

    public static Stream<AbstractCuratedModifier> getValidCuratedMods(MonsterGroup group) {
        return getCuratedMods().filter(mod -> mod.canApplyTo(group));
    }

    public static Stream<AbstractCuratedModifier> getCuratedModsOfRarity(Modifier.ModifierRarity rarity) {
        return curatedMods.get(rarity).stream().filter(ChimeraMonstersConfig::isModifierEnabled);
    }

    public static Stream<AbstractCuratedModifier> getValidCuratedModsOfRarity(MonsterGroup group, Modifier.ModifierRarity rarity) {
        return getCuratedModsOfRarity(rarity).filter(mod -> mod.canApplyTo(group));
    }

    public static Stream<AbstractAddonModifier> getAddonMods() {
        return addonMods.values().stream().flatMap(Collection::stream).filter(ChimeraMonstersConfig::isModifierEnabled);
    }

    public static Stream<AbstractAddonModifier> getValidAddonMods(MonsterGroup group) {
        return getAddonMods().filter(mod -> mod.canApplyTo(group));
    }

    public static Stream<AbstractAddonModifier> getAddonModsOfRarity(Modifier.ModifierRarity rarity) {
        return addonMods.get(rarity).stream().filter(ChimeraMonstersConfig::isModifierEnabled);
    }

    public static Stream<AbstractAddonModifier> getValidAddonModsOfRarity(MonsterGroup group, Modifier.ModifierRarity rarity) {
        return getAddonModsOfRarity(rarity).filter(mod -> mod.canApplyTo(group));
    }

    public static void handleRegistry(Modifier<?> modifier) {
        if (!modifier.identifier().isEmpty()) {
            modifierMap.put(modifier.identifier(), modifier);
        } else {
            ChimeraMonstersMod.logger.warn("Modifier "+ modifier +" does not set an identifier, Chimera Monsters can not add this mod via console commands!");
        }
        if (modifier instanceof AbstractMonsterModifier) {
            if (!monsterMods.containsKey(modifier.getModRarity())) {
                monsterMods.put(modifier.getModRarity(), new ArrayList<>());
            }
            monsterMods.get(modifier.getModRarity()).add((AbstractMonsterModifier) modifier);
        } else if (modifier instanceof AbstractThemedModifier) {
            if (!themedMods.containsKey(modifier.getModRarity())) {
                themedMods.put(modifier.getModRarity(), new ArrayList<>());
            }
            themedMods.get(modifier.getModRarity()).add((AbstractThemedModifier) modifier);
        } else if (modifier instanceof AbstractCuratedModifier) {
            if (!curatedMods.containsKey(modifier.getModRarity())) {
                curatedMods.put(modifier.getModRarity(), new ArrayList<>());
            }
            curatedMods.get(modifier.getModRarity()).add((AbstractCuratedModifier) modifier);
        } else if (modifier instanceof AbstractAddonModifier) {
            if (!addonMods.containsKey(modifier.getModRarity())) {
                addonMods.put(modifier.getModRarity(), new ArrayList<>());
            }
            addonMods.get(modifier.getModRarity()).add((AbstractAddonModifier) modifier);
        }
    }
}
