package ChimeraMonsters;

import ChimeraMonsters.commands.Monster;
import ChimeraMonsters.modifiers.AbstractModifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.ui.TopPanelExplainer;
import ChimeraMonsters.util.KeywordManager;
import basemod.*;
import basemod.devcommands.ConsoleCommand;
import basemod.interfaces.*;
import basemod.patches.whatmod.WhatMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

@SpireInitializer
public class ChimeraMonstersMod implements
        EditStringsSubscriber,
        PostInitializeSubscriber,
        EditKeywordsSubscriber,
        OnStartBattleSubscriber,
        PostRenderSubscriber {
    public static final Logger logger = LogManager.getLogger(ChimeraMonstersMod.class.getName());
    public static final HashMap<AbstractModifier<?>, String> crossoverMap = new HashMap<>();
    public static final HashMap<String, Integer> crossoverSizeMap = new HashMap<>();
    private static String modID;

    //Monster Information
    public static final HashMap<Class<?>, String> idMap = new HashMap<>();
    public static final HashMap<Class<?>, String> modIDMap = new HashMap<>();
    public static final HashMap<String, AbstractMonster> dummyMonsterMap = new HashMap<>();
    public static final HashMap<String, String> encounterMap = new HashMap<>();

    // =============== INPUT TEXTURE LOCATION =================

    // =============== /INPUT TEXTURE LOCATION/ =================
    
    
    // =============== SUBSCRIBE, INITIALIZE =================
    
    public ChimeraMonstersMod() {
        logger.info("Subscribe to BaseMod hooks");
        BaseMod.subscribe(this);
        setModID("ChimeraMonsters");
        logger.info("Done subscribing");

        logger.info("Adding mod settings");
        ChimeraMonstersConfig.loadSpireConfig();
        logger.info("Done adding mod settings");
    }

    public static void registerMod(String modID, String labelText) {
        if (!ChimeraMonstersConfig.chimeraMonstersCrossoverConfig.has(modID)) {
            logger.info("Created config for modID: "+modID);
            ChimeraMonstersConfig.chimeraMonstersCrossoverConfig.setBool(modID, true);
        }
        ChimeraMonstersConfig.crossoverEnableMap.put(modID, ChimeraMonstersConfig.chimeraMonstersCrossoverConfig.getBool(modID));
        ChimeraMonstersSettingsPanel.crossoverLabelMap.put(modID, labelText);
        ChimeraMonstersSettingsPanel.makeModToggler(modID, labelText);
        logger.info("Loaded config for modID: "+modID);
    }

    public static void registerModifier(AbstractModifier<?> a, String modID) {
        if (!ChimeraMonstersConfig.crossoverEnableMap.containsKey(modID)) {
            logger.warn("Modifier "+a+" with modID "+modID+" does not match any registered configs, Chimera Monsters can not manage the spawning of this mod! Please call registerMod with your ID to set up a config.");
        }
        crossoverMap.put(a, modID);
        crossoverSizeMap.merge(modID, 1, Integer::sum);
        ChimeraMonstersController.handleRegistry(a);
        ChimeraMonstersConfig.syncDisableState(a);
    }

    public static void registerCustomBan(String modifierID, Predicate<AbstractMonster> banIf) {
        ChimeraMonstersConfig.customBanChecks.put(modifierID, ChimeraMonstersConfig.customBanChecks.getOrDefault(modifierID, c -> false).or(banIf));
    }

    public static void registerCustomBanInternal(String modifierID, Predicate<AbstractMonster> banIf) {
        ChimeraMonstersConfig.customBanChecks.put(modifierID, ChimeraMonstersConfig.customBanChecks.getOrDefault(modifierID, c -> false).or(banIf));
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

        logger.info("Misc setup");
        ChimeraMonstersController.explainer = new TopPanelExplainer();
        logger.info("Done misc setup");
    }

    static void onSetupSettingsPanel(String label) {
        logger.info("Loading monster modifiers...");

        registerMod(modID, label);
        new AutoAdd(modID)
                .packageFilter("ChimeraMonsters.modifiers")
                .any(AbstractModifier.class, (info, modifier) -> registerModifier(modifier, modID));
        logger.info("Done loading monster modifiers");
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
        loadLocalizedStrings(UIStrings.class, "GroupModifierStrings");
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

    }

}
