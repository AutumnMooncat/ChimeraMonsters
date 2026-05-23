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

    public interface TypedSetting<T> {
        T getVal();
        void setVal(T val);
    }

    public enum BoolSetting implements TypedSetting<Boolean> {
        ENABLE_MOD("enableMods", true),
        ENABLE_SHADERS("enableShaders", true),
        ENABLE_TOOLTIPS("enableTooltips", false),
        SHOW_BREAKDOWN("showBreakdown", true);

        private final String key;
        private boolean val;

        BoolSetting(String key, boolean val) {
            this.key = key;
            this.val = val;
        }

        @Override
        public Boolean getVal() {
            return val;
        }

        @Override
        public void setVal(Boolean val) {
            this.val = val;
            chimeraMonstersConfig.setBool(key, val);
            try {
                chimeraMonstersConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public enum IntSetting implements TypedSetting<Integer> {
        MOD_CHANCE("modChance", 10),
        ROLL_ATTEMPTS("rollAttempts", 1),
        COMMON_WEIGHT("commonWeight", 4),
        UNCOMMON_WEIGHT("uncommonWeight", 3),
        RARE_WEIGHT("rareWeight", 2),
        RARITY_BIAS("rarityBias", 1),
        ENHANCED_WEIGHT("modifiedFightWeight", 3),
        THEMED_WEIGHT("thematicFightWeight", 2),
        CURATED_WEIGHT("curatedFightWeight", 1),
        VANILLA_WEIGHT("unmodifiedFightWeight", 0);

        private final String key;
        private int val;

        IntSetting(String key, int val) {
            this.key = key;
            this.val = val;
        }

        @Override
        public Integer getVal() {
            return val;
        }

        @Override
        public void setVal(Integer val) {
            this.val = val;
            chimeraMonstersConfig.setInt(key, val);
            try {
                chimeraMonstersConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadSpireConfig() {
        Properties chimeraMonstersDefaultSettings = getDefaultSettings();
        try {
            chimeraMonstersConfig = new SpireConfig(ChimeraMonstersMod.getModID(), FILE_NAME, chimeraMonstersDefaultSettings);
            chimeraMonstersCrossoverConfig = new SpireConfig(ChimeraMonstersMod.getModID(), CROSSOVER_FILE_NAME);
            chimeraMonstersDisabledModifierConfig = new SpireConfig(ChimeraMonstersMod.getModID(), DISABLED_MODIFIER_FILE_NAME);

            for (BoolSetting setting : BoolSetting.values()) {
                setting.val = chimeraMonstersConfig.getBool(setting.key);
            }

            for (IntSetting setting : IntSetting.values()) {
                setting.val = chimeraMonstersConfig.getInt(setting.key);
            }

            ChimeraMonstersMod.logger.info("Chimera Monsters config loaded");
        } catch (IOException e) {
            ChimeraMonstersMod.logger.error("Chimera Monsters SpireConfig initialization failed:");
            e.printStackTrace();
        }
    }

    private static Properties getDefaultSettings() {
        Properties chimeraMonstersDefaultSettings = new Properties();
        for (BoolSetting setting : BoolSetting.values()) {
            chimeraMonstersDefaultSettings.setProperty(setting.key, Boolean.toString(setting.getVal()));
        }
        for (IntSetting setting : IntSetting.values()) {
            chimeraMonstersDefaultSettings.setProperty(setting.key, String.valueOf(setting.getVal()));
        }
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
