package ChimeraMonsters;

import ChimeraMonsters.modifiers.AbstractModifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.function.Predicate;

public class ChimeraMonstersConfig {
    public static final HashMap<String, Boolean> crossoverEnableMap = new HashMap<>();
    public static final HashSet<AbstractModifier<?>> disabledModifiers = new HashSet<>();
    public static final HashMap<String, Predicate<AbstractMonster>> customBanChecks = new HashMap<>();
    public static SpireConfig chimeraMonstersConfig;
    public static SpireConfig chimeraMonstersCrossoverConfig;
    public static SpireConfig chimeraMonstersDisabledModifierConfig;
    public static String FILE_NAME = "ChimeraMonstersConfig";
    public static String CROSSOVER_FILE_NAME = "ChimeraMonstersCrossoverConfig";
    public static String DISABLED_MODIFIER_FILE_NAME = "ChimeraMonstersDisabledConfig";
    public static final String UNMANAGED_ID = "UnmanagedChimeraMonsterID";

    public static final String ENABLE_MODS_SETTING = "enableMods";
    public static boolean enableMods = true;

    public static final String MOD_PROBABILITY = "modChance";
    public static int modProbabilityPercent = 10;

    public static final String MODIFIED_FIGHT_WEIGHT = "modifiedFightWeight";
    public static int modifiedFightWeight = 3;

    public static final String THEMED_FIGHT_WEIGHT = "thematicFightWeight";
    public static int themedFightWeight = 2;

    public static final String CURATED_FIGHT_WEIGHT = "curatedFightWeight";
    public static int curatedFightWeight = 1;

    public static final String UNMODIFIED_FIGHT_WEIGHT = "unmodifiedFightWeight";
    public static int unmodifiedFightWeight = 0;

    public static final String COMMON_WEIGHT = "commonWeight";
    public static int commonWeight = 4;

    public static final String UNCOMMON_WEIGHT = "uncommonWeight";
    public static int uncommonWeight = 3;

    public static final String RARE_WEIGHT = "rareWeight";
    public static int rareWeight = 2;

    public static final String RARITY_BIAS = "rarityBias";
    public static int rarityBias = 1;

    public static final String ENABLE_TOOLTIPS = "enableTooltips";
    public static boolean enableTooltips = true;

    public static final String ROLL_ATTEMPTS = "rollAttempts";
    public static int rollAttempts = 1;

    public static final String SHOW_BREAKDOWN = "showBreakdown";
    public static boolean showBreakdown = false;

    public static final String ENABLE_SHADERS = "enableShaders";
    public static boolean enableShaders = true;

    public static void loadSpireConfig() {
        Properties chimeraMonstersDefaultSettings = getDefaultSettings();
        try {
            ChimeraMonstersConfig.chimeraMonstersConfig = new SpireConfig(ChimeraMonstersMod.getModID(), ChimeraMonstersConfig.FILE_NAME, chimeraMonstersDefaultSettings);
            ChimeraMonstersConfig.chimeraMonstersCrossoverConfig = new SpireConfig(ChimeraMonstersMod.getModID(), ChimeraMonstersConfig.CROSSOVER_FILE_NAME);
            ChimeraMonstersConfig.chimeraMonstersDisabledModifierConfig = new SpireConfig(ChimeraMonstersMod.getModID(), ChimeraMonstersConfig.DISABLED_MODIFIER_FILE_NAME);
            ChimeraMonstersConfig.enableMods = ChimeraMonstersConfig.chimeraMonstersConfig.getBool(ChimeraMonstersConfig.ENABLE_MODS_SETTING);
            ChimeraMonstersConfig.modProbabilityPercent = ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.MOD_PROBABILITY);
            ChimeraMonstersConfig.commonWeight = ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.COMMON_WEIGHT);
            ChimeraMonstersConfig.uncommonWeight = ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.UNCOMMON_WEIGHT);
            ChimeraMonstersConfig.rareWeight = ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.RARE_WEIGHT);
            ChimeraMonstersConfig.rarityBias = ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.RARITY_BIAS);
            ChimeraMonstersConfig.enableTooltips = ChimeraMonstersConfig.chimeraMonstersConfig.getBool(ChimeraMonstersConfig.ENABLE_TOOLTIPS);
            ChimeraMonstersConfig.rollAttempts = ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.ROLL_ATTEMPTS);
            ChimeraMonstersConfig.showBreakdown = ChimeraMonstersConfig.chimeraMonstersConfig.getBool(ChimeraMonstersConfig.SHOW_BREAKDOWN);
            ChimeraMonstersConfig.enableShaders = ChimeraMonstersConfig.chimeraMonstersConfig.getBool(ChimeraMonstersConfig.ENABLE_SHADERS);
            ChimeraMonstersConfig.modifiedFightWeight = ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.MODIFIED_FIGHT_WEIGHT);
            ChimeraMonstersConfig.unmodifiedFightWeight = ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.UNMODIFIED_FIGHT_WEIGHT);
            ChimeraMonstersConfig.curatedFightWeight = ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.CURATED_FIGHT_WEIGHT);
            ChimeraMonstersConfig.themedFightWeight = ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.THEMED_FIGHT_WEIGHT);
            ChimeraMonstersMod.logger.info("Chimera Monsters config loaded");
        } catch (IOException e) {
            ChimeraMonstersMod.logger.error("Chimera Monsters SpireConfig initialization failed:");
            e.printStackTrace();
        }
    }

    private static Properties getDefaultSettings() {
        Properties chimeraMonstersDefaultSettings = new Properties();
        chimeraMonstersDefaultSettings.setProperty(ChimeraMonstersConfig.ENABLE_MODS_SETTING, Boolean.toString(ChimeraMonstersConfig.enableMods));
        chimeraMonstersDefaultSettings.setProperty(ChimeraMonstersConfig.MOD_PROBABILITY, String.valueOf(ChimeraMonstersConfig.modProbabilityPercent));
        chimeraMonstersDefaultSettings.setProperty(ChimeraMonstersConfig.COMMON_WEIGHT, String.valueOf(ChimeraMonstersConfig.commonWeight));
        chimeraMonstersDefaultSettings.setProperty(ChimeraMonstersConfig.UNCOMMON_WEIGHT, String.valueOf(ChimeraMonstersConfig.uncommonWeight));
        chimeraMonstersDefaultSettings.setProperty(ChimeraMonstersConfig.RARE_WEIGHT, String.valueOf(ChimeraMonstersConfig.rareWeight));
        chimeraMonstersDefaultSettings.setProperty(ChimeraMonstersConfig.RARITY_BIAS, String.valueOf(ChimeraMonstersConfig.rarityBias));
        chimeraMonstersDefaultSettings.setProperty(ChimeraMonstersConfig.ENABLE_TOOLTIPS, Boolean.toString(ChimeraMonstersConfig.enableTooltips));
        chimeraMonstersDefaultSettings.setProperty(ChimeraMonstersConfig.ROLL_ATTEMPTS, String.valueOf(ChimeraMonstersConfig.rollAttempts));
        chimeraMonstersDefaultSettings.setProperty(ChimeraMonstersConfig.SHOW_BREAKDOWN, Boolean.toString(ChimeraMonstersConfig.showBreakdown));
        chimeraMonstersDefaultSettings.setProperty(ChimeraMonstersConfig.ENABLE_SHADERS, Boolean.toString(ChimeraMonstersConfig.enableShaders));
        chimeraMonstersDefaultSettings.setProperty(ChimeraMonstersConfig.MODIFIED_FIGHT_WEIGHT, String.valueOf(ChimeraMonstersConfig.modifiedFightWeight));
        chimeraMonstersDefaultSettings.setProperty(ChimeraMonstersConfig.UNMODIFIED_FIGHT_WEIGHT, String.valueOf(ChimeraMonstersConfig.unmodifiedFightWeight));
        chimeraMonstersDefaultSettings.setProperty(ChimeraMonstersConfig.CURATED_FIGHT_WEIGHT, String.valueOf(ChimeraMonstersConfig.curatedFightWeight));
        chimeraMonstersDefaultSettings.setProperty(ChimeraMonstersConfig.THEMED_FIGHT_WEIGHT, String.valueOf(ChimeraMonstersConfig.themedFightWeight));
        return chimeraMonstersDefaultSettings;
    }

    public static void setModifierStatus(AbstractMonsterModifier modifier, boolean disabled) {
        if (disabled && modifier.getModRarity() != AbstractMonsterModifier.ModifierRarity.SPECIAL) {
            disabledModifiers.add(modifier);
            chimeraMonstersDisabledModifierConfig.setBool(modifier.identifier(), true);
        } else {
            disabledModifiers.remove(modifier);
            chimeraMonstersDisabledModifierConfig.remove(modifier.identifier());
        }
        try {
            chimeraMonstersDisabledModifierConfig.save();
        } catch (IOException e) {
            ChimeraMonstersMod.logger.error("Chimera Monsters Modifier Config failed:");
            e.printStackTrace();
        }
    }

    public static boolean isModifierEnabled(AbstractModifier<?> modifier) {
        return !disabledModifiers.contains(modifier) && crossoverEnableMap.getOrDefault(ChimeraMonstersMod.crossoverMap.getOrDefault(modifier, UNMANAGED_ID), true);
    }

    public static void syncDisableState(AbstractModifier<?> a) {
        if (chimeraMonstersDisabledModifierConfig.has(a.identifier())) {
            if (chimeraMonstersDisabledModifierConfig.getBool(a.identifier()) && a.getModRarity() != AbstractMonsterModifier.ModifierRarity.SPECIAL) {
                disabledModifiers.add(a);
            } else {
                chimeraMonstersDisabledModifierConfig.remove(a.identifier());
                try {
                    chimeraMonstersDisabledModifierConfig.save();
                } catch (IOException e) {
                    ChimeraMonstersMod.logger.error("Chimera Monster Modifier Config failed:");
                    e.printStackTrace();
                }
            }
        }
    }
}
