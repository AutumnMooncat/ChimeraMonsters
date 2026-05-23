package ChimeraMonsters;

import ChimeraMonsters.modifiers.AbstractModifier;
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
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChimeraMonstersController {
    //Mod Lists
    public static final HashMap<AbstractModifier.ModifierRarity, ArrayList<AbstractMonsterModifier>> monsterMods = new HashMap<>();
    public static final HashMap<AbstractModifier.ModifierRarity, ArrayList<AbstractThemedModifier>> thematicGroupMods = new HashMap<>();
    public static final HashMap<String, AbstractModifier<?>> modifierMap = new HashMap<>();

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

    public static List<AbstractModifier<?>> currentCombatModifiers() {
        List<AbstractModifier<?>> mods = new ArrayList<>();
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
        if (getValidMonsterModsOfRarity(monster, context, AbstractModifier.ModifierRarity.COMMON).findAny().isPresent()) {
            return true;
        }
        if (getValidMonsterModsOfRarity(monster, context, AbstractModifier.ModifierRarity.UNCOMMON).findAny().isPresent()) {
            return true;
        }
        if (getValidMonsterModsOfRarity(monster, context, AbstractModifier.ModifierRarity.RARE).findAny().isPresent()) {
            return true;
        }
        return false;
    }

    public static boolean canReceiveModifier(MonsterGroup group) {
        if (getValidThematicModsOfRarity(group, AbstractModifier.ModifierRarity.COMMON).findAny().isPresent()) {
            return true;
        }
        if (getValidThematicModsOfRarity(group, AbstractModifier.ModifierRarity.UNCOMMON).findAny().isPresent()) {
            return true;
        }
        if (getValidThematicModsOfRarity(group, AbstractModifier.ModifierRarity.RARE).findAny().isPresent()) {
            return true;
        }
        return false;
    }

    public static Stream<AbstractMonsterModifier> getMonsterModsOfRarity(AbstractModifier.ModifierRarity rarity) {
        return monsterMods.get(rarity).stream().filter(ChimeraMonstersConfig::isModifierEnabled);
    }

    public static Stream<AbstractMonsterModifier> getValidMonsterModsOfRarity(AbstractMonster mon, MonsterGroup context, AbstractModifier.ModifierRarity rarity) {
        return getMonsterModsOfRarity(rarity).filter(mod -> mod.canApplyTo(mon, context));
    }

    public static Stream<AbstractThemedModifier> getThematicModsOfRarity(AbstractModifier.ModifierRarity rarity) {
        return thematicGroupMods.get(rarity).stream().filter(ChimeraMonstersConfig::isModifierEnabled);
    }

    public static Stream<AbstractThemedModifier> getValidThematicModsOfRarity(MonsterGroup group, AbstractModifier.ModifierRarity rarity) {
        return getThematicModsOfRarity(rarity).filter(mod -> mod.canApplyTo(group));
    }

    public static void handleRegistry(AbstractModifier<?> modifier) {
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
            if (!thematicGroupMods.containsKey(modifier.getModRarity())) {
                thematicGroupMods.put(modifier.getModRarity(), new ArrayList<>());
            }
            thematicGroupMods.get(modifier.getModRarity()).add((AbstractThemedModifier) modifier);
        }
    }
}
