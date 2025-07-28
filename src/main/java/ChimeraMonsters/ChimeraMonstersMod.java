package ChimeraMonsters;

import ChimeraMonsters.commands.Monster;
import ChimeraMonsters.curatedFights.AbstractCuratedFight;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.patches.MonsterModifierFieldPatches;
import ChimeraMonsters.powers.ModifierExplainerPower;
import ChimeraMonsters.ui.BiggerModButton;
import ChimeraMonsters.ui.CenteredModLabel;
import ChimeraMonsters.ui.ModLabeledToggleTooltipButton;
import ChimeraMonsters.util.FightModificationManager;
import ChimeraMonsters.util.KeywordManager;
import ChimeraMonsters.util.TextureLoader;
import basemod.*;
import basemod.devcommands.ConsoleCommand;
import basemod.interfaces.*;
import basemod.patches.whatmod.WhatMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.beyond.Exploder;
import com.megacrit.cardcrawl.monsters.beyond.Repulsor;
import com.megacrit.cardcrawl.monsters.beyond.Spiker;
import com.megacrit.cardcrawl.monsters.exordium.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CtBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SpireInitializer
public class ChimeraMonstersMod implements
        EditStringsSubscriber,
        PostInitializeSubscriber,
        EditKeywordsSubscriber,
        OnStartBattleSubscriber,
        PostRenderSubscriber {
    public static final Logger logger = LogManager.getLogger(ChimeraMonstersMod.class.getName());
    private static String modID;

    public static SpireConfig chimeraMonstersConfig;
    public static SpireConfig chimeraMonstersCrossoverConfig;
    public static SpireConfig chimeraMonstersDisabledModifierConfig;
    public static String FILE_NAME = "ChimeraMonstersConfig";
    public static String CROSSOVER_FILE_NAME = "ChimeraMonstersCrossoverConfig";
    public static String DISABLED_MODIFIER_FILE_NAME;

    public static final String ENABLE_MODS_SETTING = "enableMods";
    public static boolean enableMods = true;

    public static final String MOD_PROBABILITY = "modChance";
    public static int modProbabilityPercent = 10;

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

    //Mod Lists
    public static final ArrayList<AbstractMonsterModifier> commonMods = new ArrayList<>();
    public static final ArrayList<AbstractMonsterModifier> uncommonMods = new ArrayList<>();
    public static final ArrayList<AbstractMonsterModifier> rareMods = new ArrayList<>();
    public static final ArrayList<AbstractMonsterModifier> specialMods = new ArrayList<>();
    public static final HashMap<String, AbstractMonsterModifier> modMap = new HashMap<>();
    public static final HashMap<String, AbstractCuratedFight> curatedFightMap = new HashMap<>();
    public static final HashMap<AbstractMonsterModifier, String> crossoverMap = new HashMap<>();
    public static final HashMap<String, String> crossoverLabelMap = new HashMap<>();
    public static final HashMap<String, Integer> crossoverSizeMap = new HashMap<>();
    public static final HashMap<String, Boolean> crossoverEnableMap = new HashMap<>();
    public static final HashSet<AbstractMonsterModifier> disabledModifiers = new HashSet<>();
    public static final HashMap<String, Predicate<AbstractMonster>> customBanChecks = new HashMap<>();
    public static final String UNMANAGED_ID = "UnmanagedChimeraMonsterID";

    //Monster Information
    public static final HashMap<Class<?>, String> idMap = new HashMap<>();
    public static final HashMap<Class<?>, String> modIDMap = new HashMap<>();
    public static final HashMap<String, AbstractMonster> dummyMonsterMap = new HashMap<>();
    public static final HashMap<String, String> encounterMap = new HashMap<>();

    //This is for the in-game mod settings panel.
    public static UIStrings uiStrings;
    public static UIStrings crossoverUIStrings;
    public static String[] TEXT;
    public static String ENABLE_TEXT;
    public static String MOD_CHANCE_TEXT;
    public static String COMMON_WEIGHT_TEXT;
    public static String UNCOMMON_WEIGHT_TEXT;
    public static String RARE_WEIGHT_TEXT;
    public static String WEIGHT_BIAS_TEXT;
    public static String ENABLE_TIPS_TEXT;
    public static String ROLL_ATTEMPTS_TEXT;
    public static String SHOW_ANALYSIS_TEXT;
    public static String ENABLE_SHADERS_TEXT;
    public static String[] EXTRA_TEXT;
    private static final String AUTHOR = "Mistress Autumn, Mindbomber";

    public static ModPanel settingsPanel;
    public static ModLabel noCrossoverLabel;
    public static HashMap<Integer, ArrayList<IUIElement>> pages = new HashMap<>();
    public static float LAYOUT_Y = 760f;
    public static final float LAYOUT_X = 400f;
    public static final float SPACING_Y = 43f;
    public static final float FULL_PAGE_Y = (SPACING_Y * 13);
    public static float deltaY = 0;
    public static int currentPage = 0;
    
    // =============== INPUT TEXTURE LOCATION =================
    
    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    public static final String BADGE_IMAGE = "ChimeraMonstersResources/images/Badge.png";
    
    // =============== /INPUT TEXTURE LOCATION/ =================
    
    
    // =============== SUBSCRIBE, INITIALIZE =================
    
    public ChimeraMonstersMod() {
        logger.info("Subscribe to BaseMod hooks");
        
        BaseMod.subscribe(this);
      
        setModID("ChimeraMonsters");
        
        logger.info("Done subscribing");

        logger.info("Adding mod settings");
        Properties chimeraMonstersDefaultSettings = new Properties();
        chimeraMonstersDefaultSettings.setProperty(ENABLE_MODS_SETTING, Boolean.toString(enableMods));
        chimeraMonstersDefaultSettings.setProperty(MOD_PROBABILITY, String.valueOf(modProbabilityPercent));
        chimeraMonstersDefaultSettings.setProperty(COMMON_WEIGHT, String.valueOf(commonWeight));
        chimeraMonstersDefaultSettings.setProperty(UNCOMMON_WEIGHT, String.valueOf(uncommonWeight));
        chimeraMonstersDefaultSettings.setProperty(RARE_WEIGHT, String.valueOf(rareWeight));
        chimeraMonstersDefaultSettings.setProperty(RARITY_BIAS, String.valueOf(rarityBias));
        chimeraMonstersDefaultSettings.setProperty(ENABLE_TOOLTIPS, Boolean.toString(enableTooltips));
        chimeraMonstersDefaultSettings.setProperty(ROLL_ATTEMPTS, String.valueOf(rollAttempts));
        chimeraMonstersDefaultSettings.setProperty(SHOW_BREAKDOWN, Boolean.toString(showBreakdown));
        chimeraMonstersDefaultSettings.setProperty(ENABLE_SHADERS, Boolean.toString(enableShaders));
        try {
            chimeraMonstersConfig = new SpireConfig(modID, FILE_NAME, chimeraMonstersDefaultSettings);
            chimeraMonstersCrossoverConfig = new SpireConfig(modID, CROSSOVER_FILE_NAME);
            chimeraMonstersDisabledModifierConfig = new SpireConfig(modID, DISABLED_MODIFIER_FILE_NAME);
            enableMods = chimeraMonstersConfig.getBool(ENABLE_MODS_SETTING);
            modProbabilityPercent = chimeraMonstersConfig.getInt(MOD_PROBABILITY);
            commonWeight = chimeraMonstersConfig.getInt(COMMON_WEIGHT);
            uncommonWeight = chimeraMonstersConfig.getInt(UNCOMMON_WEIGHT);
            rareWeight = chimeraMonstersConfig.getInt(RARE_WEIGHT);
            rarityBias = chimeraMonstersConfig.getInt(RARITY_BIAS);
            enableTooltips = chimeraMonstersConfig.getBool(ENABLE_TOOLTIPS);
            rollAttempts = chimeraMonstersConfig.getInt(ROLL_ATTEMPTS);
            showBreakdown = chimeraMonstersConfig.getBool(SHOW_BREAKDOWN);
            enableShaders = chimeraMonstersConfig.getBool(ENABLE_SHADERS);
        } catch (IOException e) {
            logger.error("Chimera Monsters SpireConfig initialization failed:");
            e.printStackTrace();
        }
        logger.info("Chimera Monsters config loaded");

        logger.info("Done adding mod settings");
        
    }

    public static void registerMod(String modID, String labelText) {
        if (!chimeraMonstersCrossoverConfig.has(modID)) {
            logger.info("Created config for modID: "+modID);
            chimeraMonstersCrossoverConfig.setBool(modID, true);
        }
        crossoverEnableMap.put(modID, chimeraMonstersCrossoverConfig.getBool(modID));
        crossoverLabelMap.put(modID, labelText);
        ModLabeledToggleButton enableCrossoverButton = new ModLabeledToggleButton(labelText,LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                chimeraMonstersCrossoverConfig.getBool(modID), settingsPanel,
                (label) -> {
                    label.text = crossoverLabelMap.get(modID) + " (" + crossoverSizeMap.get(modID) + " " + crossoverUIStrings.TEXT[2] + ")";
                },
                (button) -> {
                    chimeraMonstersCrossoverConfig.setBool(modID, button.enabled);
                    crossoverEnableMap.put(modID, button.enabled);
                    try {
                        chimeraMonstersCrossoverConfig.save();} catch (IOException e) {e.printStackTrace();}
                });
        registerUIElement(enableCrossoverButton);
        logger.info("Loaded config for modID: "+modID);
    }

    public static void registerMonsterModifier(AbstractMonsterModifier a, String modID) {
        if (!Objects.equals(modID, UNMANAGED_ID) && !crossoverEnableMap.containsKey(modID)) {
            logger.warn("Modifier "+a+" with modID "+modID+" does not match any registered configs, Chimera Monsters can not manage the spawning of this mod! Please call registerMod with your ID to set up a config.");
        }
        crossoverMap.put(a, modID);
        crossoverSizeMap.merge(modID, 1, Integer::sum);
        if (!a.identifier().isEmpty()) {
            modMap.put(a.identifier(), a);
        } else {
            logger.warn("Modifier "+ a +" does not set an identifier, Chimera Monsters can not add this mod via console commands!");
        }
        switch (a.getModRarity()) {
            case COMMON:
                commonMods.add(a);
                break;
            case UNCOMMON:
                uncommonMods.add(a);
                break;
            case RARE:
                rareMods.add(a);
                break;
            case SPECIAL:
                specialMods.add(a);
                break;
        }
        if (chimeraMonstersDisabledModifierConfig.has(a.identifier())) {
            if (chimeraMonstersDisabledModifierConfig.getBool(a.identifier()) && a.getModRarity() != AbstractMonsterModifier.ModifierRarity.SPECIAL) {
                disabledModifiers.add(a);
            } else {
                chimeraMonstersDisabledModifierConfig.remove(a.identifier());
                try {
                    chimeraMonstersDisabledModifierConfig.save();
                } catch (IOException e) {
                    logger.error("Chimera Monster Modifier Config failed:");
                    e.printStackTrace();
                }
            }
        }
    }

    public static void registerCuratedFights(AbstractCuratedFight a, String modID) {
       //TODO: crossover logic
        if (!a.identifier().isEmpty()) {
            curatedFightMap.put(a.identifier(), a);
        } else {
            //TODO: Make console command for curated fights. Use MonsterSpawn+MonsterApply command logic
            logger.warn("Curated Fight "+ a +" does not set an identifier, Chimera Monsters can not spawn this curated fight via console commands!");
        }
        //TODO: Config Disabling
        /*if (chimeraMonstersDisabledModifierConfig.has(a.identifier())) {
            if (chimeraMonstersDisabledModifierConfig.getBool(a.identifier()) && a.getModRarity() != AbstractMonsterModifier.ModifierRarity.SPECIAL) {
                disabledModifiers.add(a);
            } else {
                chimeraMonstersDisabledModifierConfig.remove(a.identifier());
                try {
                    chimeraMonstersDisabledModifierConfig.save();
                } catch (IOException e) {
                    logger.error("Chimera Monster Modifier Config failed:");
                    e.printStackTrace();
                }
            }
        }*/
    }

    public static void registerCustomBan(String modifierID, Predicate<AbstractMonster> banIf) {
        customBanChecks.put(modifierID, customBanChecks.getOrDefault(modifierID, c -> false).or(banIf));
    }

    public static void setModID(String ID) {
        modID = ID;
    }
    
    public static String getModID() {
        return modID;
    }
    
    public static void initialize() {
        logger.info("========================= Initializing Chimera Monsters. =========================");
        ChimeraMonstersMod chimeraMonstersMod = new ChimeraMonstersMod();
        logger.info("========================= /Chimera Monsters Initialized/ =========================");
    }

    public static String makePath(String resourcePath) {
        return modID + "Resources/" + resourcePath;
    }

    public static String makeImagePath(String resourcePath) {
        return modID + "Resources/images/" + resourcePath;
    }

    public static String makeRelicPath(String resourcePath) {
        return modID + "Resources/images/relics/" + resourcePath;
    }

    public static String makePowerPath(String resourcePath) {
        return modID + "Resources/images/powers/" + resourcePath;
    }

    public static String makeCardPath(String resourcePath) {
        return modID + "Resources/images/cards/" + resourcePath;
    }
    
    // =============== POST-INITIALIZE =================
    
    @Override
    public void receivePostInitialize() {
        logger.info("Setting up dev commands");

        ConsoleCommand.addCommand("monstermod", Monster.class);

        logger.info("Done setting up dev commands");

        logger.info("Finding monsters");

        findMonsters();

        logger.info("Done finding monsters");
    }

    private static void setupSettingsPanel() {
        logger.info("Loading badge image and mod options");
        settingsPanel = new ModPanel();
        float aspectRatio = (float)Settings.WIDTH/(float)Settings.HEIGHT;
        float sixteenByNine = 1920f/1080f;
        if (Settings.isFourByThree || (aspectRatio < 1.333F)) {
            LAYOUT_Y *= 1.2222f;
        } else if (Settings.isSixteenByTen) {
            LAYOUT_Y *= 1.08f;
        } else if (aspectRatio < sixteenByNine) {
            LAYOUT_Y *= 1.8888f - aspectRatio/2f;
        }


        //Grab the strings
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ModConfigs"));
        crossoverUIStrings = CardCrawlGame.languagePack.getUIString(makeID("CrossoverConfig"));
        EXTRA_TEXT = uiStrings.EXTRA_TEXT;
        TEXT = uiStrings.TEXT;
        ENABLE_TEXT = TEXT[0];
        MOD_CHANCE_TEXT = TEXT[1];
        COMMON_WEIGHT_TEXT = TEXT[2];
        UNCOMMON_WEIGHT_TEXT = TEXT[3];
        RARE_WEIGHT_TEXT = TEXT[4];
        WEIGHT_BIAS_TEXT = TEXT[5];
        ENABLE_TIPS_TEXT = TEXT[6];
        ROLL_ATTEMPTS_TEXT = TEXT[7];
        SHOW_ANALYSIS_TEXT = TEXT[8];
        ENABLE_SHADERS_TEXT = TEXT[9];
        // Create the Mod Menu

        // Load the Mod Badge
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        BaseMod.registerModBadge(badgeTexture, EXTRA_TEXT[0], AUTHOR, EXTRA_TEXT[1], settingsPanel);

        //Get the longest slider text for positioning
        ArrayList<String> labelStrings = new ArrayList<>(Arrays.asList(TEXT));
        float sliderOffset = getSliderPosition(labelStrings.subList(1,5));
        labelStrings.clear();

        //Show data?
        ModLabeledToggleTooltipButton dataButton = new ModLabeledToggleTooltipButton(SHOW_ANALYSIS_TEXT, getProbabilityData(), LAYOUT_X + 830f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                chimeraMonstersConfig.getBool(SHOW_BREAKDOWN), settingsPanel, panel -> panel.tooltip = getProbabilityData(), (button) -> {
            chimeraMonstersConfig.setBool(SHOW_BREAKDOWN, button.enabled);
            showBreakdown = button.enabled;
            try {
                chimeraMonstersConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //Enable or disable the mod entirely.
        ModLabeledToggleButton enableModsButton = new ModLabeledToggleButton(ENABLE_TEXT,LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                chimeraMonstersConfig.getBool(ENABLE_MODS_SETTING), settingsPanel, (label) -> {}, (button) -> {
            chimeraMonstersConfig.setBool(ENABLE_MODS_SETTING, button.enabled);
            enableMods = button.enabled;
            try {
                chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for probability of a mod being applied
        ModLabel probabilityLabel = new ModLabel(MOD_CHANCE_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider probabilitySlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 100, chimeraMonstersConfig.getInt(MOD_PROBABILITY), "%.0f", settingsPanel, slider -> {
            chimeraMonstersConfig.setInt(MOD_PROBABILITY, Math.round(slider.getValue()));
            modProbabilityPercent = Math.round(slider.getValue());
            try {
                chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for roll attempts
        ModLabel attemptsLabel = new ModLabel(ROLL_ATTEMPTS_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider attemptsSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                1, 3, chimeraMonstersConfig.getInt(ROLL_ATTEMPTS), "%.0f", settingsPanel, slider -> {
            chimeraMonstersConfig.setInt(ROLL_ATTEMPTS, Math.round(slider.getValue()));
            rollAttempts = Math.round(slider.getValue());
            try {
                chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for common mod weight
        ModLabel commonLabel = new ModLabel(COMMON_WEIGHT_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider commonSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 10, chimeraMonstersConfig.getInt(COMMON_WEIGHT), "%.0f", settingsPanel, slider -> {
            chimeraMonstersConfig.setInt(COMMON_WEIGHT, Math.round(slider.getValue()));
            commonWeight = Math.round(slider.getValue());
            try {
                chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for uncommon mod weight
        ModLabel uncommonLabel = new ModLabel(UNCOMMON_WEIGHT_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider uncommonSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 10, chimeraMonstersConfig.getInt(UNCOMMON_WEIGHT), "%.0f", settingsPanel, slider -> {
            chimeraMonstersConfig.setInt(UNCOMMON_WEIGHT, Math.round(slider.getValue()));
            uncommonWeight = Math.round(slider.getValue());
            try {
                chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for rare mod weight
        ModLabel rareLabel = new ModLabel(RARE_WEIGHT_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider rareSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 10, chimeraMonstersConfig.getInt(RARE_WEIGHT), "%.0f", settingsPanel, slider -> {
            chimeraMonstersConfig.setInt(RARE_WEIGHT, Math.round(slider.getValue()));
            rareWeight = Math.round(slider.getValue());
            try {
                chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for bias weight
        ModLabel biasLabel = new ModLabel(WEIGHT_BIAS_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider biasSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 5, chimeraMonstersConfig.getInt(RARITY_BIAS), "%.0f", settingsPanel, slider -> {
            chimeraMonstersConfig.setInt(RARITY_BIAS, Math.round(slider.getValue()));
            rarityBias = Math.round(slider.getValue());
            try {
                chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used enable tooltips
        ModLabeledToggleButton enableTooltipsButton = new ModLabeledToggleButton(ENABLE_TIPS_TEXT,LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                chimeraMonstersConfig.getBool(ENABLE_TOOLTIPS), settingsPanel, (label) -> {}, (button) -> {
            chimeraMonstersConfig.setBool(ENABLE_TOOLTIPS, button.enabled);
            enableTooltips = button.enabled;
            try {
                chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used enable shaders
        ModLabeledToggleButton enableShadersButton = new ModLabeledToggleButton(ENABLE_SHADERS_TEXT,LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                chimeraMonstersConfig.getBool(ENABLE_SHADERS), settingsPanel, (label) -> {}, (button) -> {
            chimeraMonstersConfig.setBool(ENABLE_SHADERS, button.enabled);
            enableTooltips = button.enabled;
            try {
                chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        registerUIElement(dataButton, false);
        registerUIElement(enableModsButton);
        registerUIElement(probabilityLabel, false);
        registerUIElement(probabilitySlider);
        registerUIElement(attemptsLabel, false);
        registerUIElement(attemptsSlider);
        registerUIElement(commonLabel, false);
        registerUIElement(commonSlider);
        registerUIElement(uncommonLabel, false);
        registerUIElement(uncommonSlider);
        registerUIElement(rareLabel, false);
        registerUIElement(rareSlider);
        registerUIElement(biasLabel, false);
        registerUIElement(biasSlider);
        registerUIElement(enableTooltipsButton);
        registerUIElement(enableShadersButton);

        CenteredModLabel pageLabel = new CenteredModLabel(crossoverUIStrings.TEXT[1], Settings.WIDTH/2f/Settings.xScale, LAYOUT_Y + 70f, settingsPanel, l -> {
            l.text = crossoverUIStrings.TEXT[1] + " " + (currentPage + 1) + "/" + (pages.size());
        });
        BiggerModButton leftButton = new BiggerModButton(Settings.WIDTH/2F/Settings.xScale - 100f - ImageMaster.CF_LEFT_ARROW.getWidth()/2F, LAYOUT_Y + 45f, -5f, ImageMaster.CF_LEFT_ARROW, settingsPanel, b -> {
            if (currentPage > 0) {
                previousPage();
            } else {
                for (int i = 0 ; i < pages.size()-1 ; i++) {
                    nextPage();
                }
            }
        });
        BiggerModButton rightButton = new BiggerModButton(Settings.WIDTH/2F/Settings.xScale + 100f - ImageMaster.CF_LEFT_ARROW.getWidth()/2F, LAYOUT_Y + 45f, -5f, ImageMaster.CF_RIGHT_ARROW, settingsPanel, b -> {
            if (currentPage < pages.size()-1) {
                nextPage();
            } else {
                for (int i = currentPage ; i > 0 ; i--) {
                    previousPage();
                }
            }
        });

        settingsPanel.addUIElement(pageLabel);
        settingsPanel.addUIElement(leftButton);
        settingsPanel.addUIElement(rightButton);

        logger.info("Done loading badge Image and mod options");

        logger.info("Loading monster modifiers...");

        registerMod(modID, crossoverUIStrings.TEXT[0]);
        new AutoAdd(modID)
                .packageFilter("ChimeraMonsters.modifiers")
                .any(AbstractMonsterModifier.class, (info, abstractAugment) -> registerMonsterModifier(abstractAugment, modID));
        new AutoAdd(modID)
                .packageFilter("ChimeraMonsters.curatedFights")
                .any(AbstractCuratedFight.class, (info, abstractAugment) -> registerCuratedFights(abstractAugment, modID));
        logger.info("Done loading monster modifiers");
    }

    private static void registerUIElement(IUIElement elem) {
        registerUIElement(elem, true);
    }

    private static void registerUIElement(IUIElement elem, boolean decrement) {
        settingsPanel.addUIElement(elem);
        if (pages.isEmpty()) {
            pages.put(0, new ArrayList<>());
        }
        int page = pages.size()-1;
        pages.get(page).add(elem);
        elem.setY(elem.getY() - deltaY);
        elem.setX(elem.getX() + (page * Settings.WIDTH)/Settings.scale);
        //elem.setY((elem.getY() - deltaY)/Settings.scale*Settings.yScale);
        //elem.setX((elem.getX()*Settings.xScale + (page * Settings.WIDTH))/Settings.scale);
        if (decrement) {
            deltaY += SPACING_Y;
            if (deltaY > FULL_PAGE_Y) {
                deltaY = 0;
                pages.put(page+1, new ArrayList<>());
            }
        }
    }

    private static void nextPage() {
        for (ArrayList<IUIElement> elems : pages.values()) {
            for (IUIElement elem : elems) {
                elem.setX(elem.getX() - Settings.WIDTH/Settings.scale);
                //elem.setX((elem.getX()*Settings.xScale - Settings.WIDTH)/Settings.scale);
            }
        }
        currentPage++;
    }

    private static void previousPage() {
        for (ArrayList<IUIElement> elems : pages.values()) {
            for (IUIElement elem : elems) {
                elem.setX(elem.getX() + Settings.WIDTH/Settings.scale);
                //elem.setX((elem.getX()*Settings.xScale + Settings.WIDTH)/Settings.scale);
            }
        }
        currentPage--;
    }

    //Get the longest text so all sliders are centered
    private static float getSliderPosition(List<String> stringsToCompare) {
        float longest = 0;
        for (String s : stringsToCompare) {
            longest = Math.max(longest, FontHelper.getWidth(FontHelper.charDescFont, s, 1f /Settings.scale));
        }
        return longest + 40f;
    }

    private static float getRollProbability(int exactly) {
        return (float) ((Math.pow(modProbabilityPercent/100f, exactly) * Math.pow(1-modProbabilityPercent/100f, rollAttempts-exactly)) * 100f * combination(rollAttempts, exactly));
    }

    private static int combination(int total, int choose) {
        return factorial(total) / (factorial(choose) * factorial(total-choose));
    }

    private static int factorial(int x) {
        if (x <= 1) {
            return 1;
        }
        return x * factorial(x-1);
    }

    private static float getBiasedWeightProbability(AbstractMonsterModifier.ModifierRarity r, boolean matches) {
        if (commonWeight + uncommonWeight + rareWeight + rarityBias == 0) {
            return 0;
        }
        switch (r) {
            case COMMON:
                return 100 * ((float) commonWeight + (matches ? rarityBias : 0)) / (commonWeight + uncommonWeight + rareWeight + rarityBias);
            case UNCOMMON:
                return 100 * ((float) uncommonWeight + (matches ? rarityBias : 0)) / (commonWeight + uncommonWeight + rareWeight + rarityBias);
            case RARE:
                return 100 * ((float) rareWeight + (matches ? rarityBias : 0)) / (commonWeight + uncommonWeight + rareWeight + rarityBias);
            case SPECIAL:
                return 0;
        }
        return 0;
    }

    private static float getWeightProbability(AbstractMonsterModifier.ModifierRarity r) {
        if (commonWeight + uncommonWeight + rareWeight == 0) {
            return 0;
        }
        switch (r) {
            case COMMON:
                return 100 * ((float) commonWeight) / (commonWeight + uncommonWeight + rareWeight);
            case UNCOMMON:
                return 100 * ((float) uncommonWeight) / (commonWeight + uncommonWeight + rareWeight);
            case RARE:
                return 100 * ((float) rareWeight) / (commonWeight + uncommonWeight + rareWeight);
            case SPECIAL:
                return 0;
        }
        return 0;
    }

    private static String getProbabilityData() {
        StringBuilder sb = new StringBuilder();
        sb.append(EXTRA_TEXT[2]);
        for (int i = 0 ; i <= rollAttempts ; i++) {
            if (i == 1) {
                sb.append(" NL #b").append(i).append(EXTRA_TEXT[3]).append(String.format("%.02f", getRollProbability(i))).append("%");
            } else {
                sb.append(" NL #b").append(i).append(EXTRA_TEXT[4]).append(String.format("%.02f", getRollProbability(i))).append("%");
            }
        }
        if (rarityBias == 0) {
            sb.append(EXTRA_TEXT[5]).append(String.format("%.02f", getWeightProbability(AbstractMonsterModifier.ModifierRarity.COMMON))).append("%");
            sb.append(EXTRA_TEXT[6]).append(String.format("%.02f", getWeightProbability(AbstractMonsterModifier.ModifierRarity.UNCOMMON))).append("%");
            sb.append(EXTRA_TEXT[7]).append(String.format("%.02f", getWeightProbability(AbstractMonsterModifier.ModifierRarity.RARE))).append("%");
        } else {
            sb.append(EXTRA_TEXT[8]);
            sb.append(EXTRA_TEXT[5]).append(String.format("%.02f", getBiasedWeightProbability(AbstractMonsterModifier.ModifierRarity.COMMON, true))).append("%");
            sb.append(EXTRA_TEXT[6]).append(String.format("%.02f", getBiasedWeightProbability(AbstractMonsterModifier.ModifierRarity.UNCOMMON, false))).append("%");
            sb.append(EXTRA_TEXT[7]).append(String.format("%.02f", getBiasedWeightProbability(AbstractMonsterModifier.ModifierRarity.RARE, false))).append("%");
            sb.append(EXTRA_TEXT[9]);
            sb.append(EXTRA_TEXT[5]).append(String.format("%.02f", getBiasedWeightProbability(AbstractMonsterModifier.ModifierRarity.COMMON, false))).append("%");
            sb.append(EXTRA_TEXT[6]).append(String.format("%.02f", getBiasedWeightProbability(AbstractMonsterModifier.ModifierRarity.UNCOMMON, true))).append("%");
            sb.append(EXTRA_TEXT[7]).append(String.format("%.02f", getBiasedWeightProbability(AbstractMonsterModifier.ModifierRarity.RARE, false))).append("%");
            sb.append(EXTRA_TEXT[10]);
            sb.append(EXTRA_TEXT[5]).append(String.format("%.02f", getBiasedWeightProbability(AbstractMonsterModifier.ModifierRarity.COMMON, false))).append("%");
            sb.append(EXTRA_TEXT[6]).append(String.format("%.02f", getBiasedWeightProbability(AbstractMonsterModifier.ModifierRarity.UNCOMMON, false))).append("%");
            sb.append(EXTRA_TEXT[7]).append(String.format("%.02f", getBiasedWeightProbability(AbstractMonsterModifier.ModifierRarity.RARE, true))).append("%");
        }
        return sb.toString();
    }
    
    // =============== / POST-INITIALIZE/ =================

    // ================ LOAD THE LOCALIZATION ===================

    private String loadLocalizationIfAvailable(String fileName) {
        if (!Gdx.files.internal(getModID() + "Resources/localization/" + Settings.language.toString().toLowerCase()+ "/" + fileName).exists()) {
            logger.info("Language: " + Settings.language.toString().toLowerCase() + ", not currently supported for " +fileName+".");
            return "eng" + "/" + fileName;
        } else {
            logger.info("Loaded Language: "+ Settings.language.toString().toLowerCase() + ", for "+fileName+".");
            return Settings.language.toString().toLowerCase() + "/" + fileName;
        }
    }

    private void loadLocalizedStrings(Class<?> stringClass, String fileName) {
        //Load English first
        BaseMod.loadCustomStringsFile(stringClass, modID + "Resources/localization/eng/"+fileName+".json");

        //Attempt loading localization
        if (!Settings.language.toString().equalsIgnoreCase("eng")) {
            String path = modID + "Resources/localization/" + Settings.language.toString().toLowerCase() + "/" + fileName + ".json";
            if (Gdx.files.internal(path).exists()) {
                BaseMod.loadCustomStringsFile(stringClass, path);
            }
        }
    }

    // ================ /LOAD THE LOCALIZATION/ ===================

    // ================ LOAD THE TEXT ===================
    
    @Override
    public void receiveEditStrings() {
        logger.info("Beginning to edit strings for mod with ID: " + getModID());
        loadLocalizedStrings(UIStrings.class, "UIStrings");
        loadLocalizedStrings(UIStrings.class, "ModifierStrings");
        loadLocalizedStrings(PowerStrings.class, "PowerStrings");
        logger.info("Done editing strings");
    }
    
    // ================ /LOAD THE TEXT/ ===================
    
    // this adds "ModName:" before the ID of any card/relic/power etc.
    // in order to avoid conflicts if any other mod uses the same ID.
    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }

    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String json = Gdx.files.internal(getModID()+"Resources/localization/"+loadLocalizationIfAvailable("KeywordStrings.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(getModID().toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
                if(keyword.NAMES.length > 0 && !keyword.ID.isEmpty()) {
                    KeywordManager.setupKeyword(keyword.ID, keyword.NAMES[0]);
                }
            }
        }
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
            logger.error("Chimera Monsters Modifier Config failed:");
            e.printStackTrace();
        }
    }

    public static boolean isModifierEnabled(AbstractMonsterModifier modifier) {
        return !disabledModifiers.contains(modifier) && crossoverEnableMap.getOrDefault(crossoverMap.getOrDefault(modifier, UNMANAGED_ID), true);
    }

    public static void applyModifier(AbstractMonster monster, AbstractMonsterModifier mod) {
        AbstractMonsterModifier copy = mod.makeCopy();
        if (CardCrawlGame.isInARun()) {
            copy.applyTo(monster);
            if (ChimeraMonstersMod.enableTooltips) {
                monster.powers.add(new ModifierExplainerPower(monster, copy.getModifierName(), copy.getModifierDescription()));
            }
        }
        MonsterModifierFieldPatches.ModifierFields.originalName.set(monster, monster.name);
        monster.name = copy.modifyName(monster);
        MonsterModifierFieldPatches.ModifierFields.receivedModifiers.get(monster).add(copy);
    }



    public static boolean canReceiveModifier(AbstractMonster monster, MonsterGroup context) {
        for (AbstractMonsterModifier a : commonMods) {
            if (a.canApplyTo(monster, context)) {
                return true;
            }
        }
        for (AbstractMonsterModifier a : uncommonMods) {
            if (a.canApplyTo(monster, context)) {
                return true;
            }
        }
        for (AbstractMonsterModifier a : rareMods) {
            if (a.canApplyTo(monster, context)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<AbstractMonsterModifier> getAllValidMonsterModifiers(AbstractMonster monster, MonsterGroup context) {
        ArrayList<AbstractMonsterModifier> validMods = new ArrayList<>();
        validMods.addAll(commonMods.stream().filter(m -> m.canApplyTo(monster, context) && isModifierEnabled(m)).collect(Collectors.toCollection(ArrayList::new)));
        validMods.addAll(uncommonMods.stream().filter(m -> m.canApplyTo(monster, context) && isModifierEnabled(m)).collect(Collectors.toCollection(ArrayList::new)));
        validMods.addAll(rareMods.stream().filter(m -> m.canApplyTo(monster, context) && isModifierEnabled(m)).collect(Collectors.toCollection(ArrayList::new)));
        return validMods;
    }

    public static AbstractMonsterModifier getTrulyRandomValidMonsterModifier(AbstractMonster monster, MonsterGroup context) {
        ArrayList<AbstractMonsterModifier> validMods = getAllValidMonsterModifiers(monster, context);
        if (!validMods.isEmpty()) {
            return validMods.get(AbstractDungeon.miscRng.random(validMods.size()-1)).makeCopy();
        }
        return null;
    }

    public static void applyTrulyRandomMonsterModifier(AbstractMonster monster, MonsterGroup context) {
        AbstractMonsterModifier mod = getTrulyRandomValidMonsterModifier(monster, context);
        if (mod != null) {
            applyModifier(monster, mod);
        }
    }

    public static void findMonsters() {
        Settings.seed = -1L;
        AbstractDungeon.generateSeeds();
        for (String encounterID : BaseMod.encounterList) {
            logger.info("Loading monsters from encounter {}", encounterID);
            MonsterGroup group = BaseMod.getMonster(encounterID);
            if (group == null) {
                group = MonsterHelper.getEncounter(encounterID);
            }
            for (AbstractMonster monster : group.monsters) {
                processMonster(encounterID, monster);
            }
        }

        registerFallback();
    }

    private static void registerFallback() {
        String fallbackID = makeID("FallbackEncounter");
        BaseMod.addMonster(fallbackID, () -> new MonsterGroup( new AbstractMonster[]{
                new GremlinFat(0, 0),
                new GremlinThief(0, 0),
                new GremlinTsundere(0, 0),
                new GremlinWizard(0, 0),
                new GremlinWarrior(0, 0),
                new Spiker(0, 0),
                new Repulsor(0, 0),
                new Exploder(0, 0),
                new SpikeSlime_S(0, 0, 0),
                new AcidSlime_S(0, 0, 0),
                new SpikeSlime_M(0, 0),
                new SpikeSlime_M(0, 0),
                new LouseNormal(0, 0),
                new LouseDefensive(0, 0)
        }));
        for (AbstractMonster monster : BaseMod.getMonster(fallbackID).monsters) {
            processMonster(fallbackID, monster);
        }
    }

    private static void processMonster(String encounterID, AbstractMonster monster) {
        Class<?> clazz = monster.getClass();
        if (!idMap.containsKey(clazz)) {
            String containingModID = WhatMod.findModID(clazz);
            String containingModName = WhatMod.findModName(clazz);
            String baseID = clazz.getSimpleName();
            if (containingModID != null) {
                baseID = containingModID + ":" + baseID;
            }
            int i = 0;
            String monsterID = baseID;
            while (dummyMonsterMap.containsKey(monsterID)) {
                i++;
                monsterID = baseID + i;
            }
            idMap.put(clazz, monsterID);
            modIDMap.put(clazz, containingModName == null ? "Vanilla" : containingModName);
            dummyMonsterMap.put(monsterID, monster);
            encounterMap.put(monsterID, encounterID);
        }
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom room) {

    }

    @Override
    public void receivePostRender(SpriteBatch spriteBatch) {
        FightModificationManager.render(spriteBatch);
    }

    @SpirePatch2(clz = CardCrawlGame.class, method = "create")
    public static class PostLoadFontsPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void load() {
            setupSettingsPanel();
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.MethodCallMatcher(AbstractCard.class, "initializeDynamicFrameWidths");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }
}
