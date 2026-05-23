package ChimeraMonsters;

import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.ui.BiggerModButton;
import ChimeraMonsters.ui.CenteredModLabel;
import ChimeraMonsters.ui.ModLabeledToggleTooltipButton;
import ChimeraMonsters.util.TextureLoader;
import basemod.*;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import javassist.CtBehavior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChimeraMonstersSettingsPanel {
    public static ModPanel settingsPanel;
    public static UIStrings uiStrings;
    public static UIStrings crossoverUIStrings;
    public static String[] TEXT;
    public static String[] EXTRA_TEXT;
    private static int textIndex = 1;
    public static final HashMap<String, String> crossoverLabelMap = new HashMap<>();

    private static final String BADGE_IMAGE = "ChimeraMonstersResources/images/Badge.png";
    private static final String AUTHOR = "Mistress Autumn, Mindbomber";
    private static float LAYOUT_X = 400f;
    private static float LAYOUT_Y = 760f;
    private static final float SPACING_Y = 43f;
    private static final float FULL_PAGE_Y = (SPACING_Y * 13);

    private static HashMap<Integer, ArrayList<IUIElement>> pages = new HashMap<>();
    private static float currentY = 0;
    private static int currentPage = 0;

    private static void setupSettingsPanel() {
        ChimeraMonstersMod.logger.info("Loading badge image and mod options");
        settingsPanel = new ModPanel();
        float aspectRatio = (float) Settings.WIDTH/(float)Settings.HEIGHT;
        float sixteenByNine = 1920f/1080f;
        if (Settings.isFourByThree || (aspectRatio < 1.333F)) {
            LAYOUT_Y *= 1.2222f;
        } else if (Settings.isSixteenByTen) {
            LAYOUT_Y *= 1.08f;
        } else if (aspectRatio < sixteenByNine) {
            LAYOUT_Y *= 1.8888f - aspectRatio/2f;
        }


        //Grab the strings
        uiStrings = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID("ModConfigs"));
        crossoverUIStrings = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID("CrossoverConfig"));
        EXTRA_TEXT = uiStrings.EXTRA_TEXT;
        TEXT = uiStrings.TEXT;

        String ENABLE_TEXT = TEXT[0];
        String MOD_CHANCE_TEXT = TEXT[1];
        String COMMON_WEIGHT_TEXT = TEXT[2];
        String UNCOMMON_WEIGHT_TEXT = TEXT[3];
        String RARE_WEIGHT_TEXT = TEXT[4];
        String WEIGHT_BIAS_TEXT = TEXT[5];
        String ENABLE_TIPS_TEXT = TEXT[6];
        String ROLL_ATTEMPTS_TEXT = TEXT[7];
        String SHOW_ANALYSIS_TEXT = TEXT[8];
        String ENABLE_SHADERS_TEXT = TEXT[9];
        String MODIFIED_WEIGHT_TEXT = TEXT[10];
        String THEMATIC_WEIGHT_TEXT = TEXT[11];
        String DEVIANT_WEIGHT_TEXT = TEXT[12];
        String UNMODIFIED_WEIGHT_TEXT = TEXT[13];
        // Create the Mod Menu

        // Load the Mod Badge
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        BaseMod.registerModBadge(badgeTexture, EXTRA_TEXT[0], AUTHOR, EXTRA_TEXT[1], settingsPanel);

        // Get the longest slider text for positioning
        ArrayList<String> labelStrings = new ArrayList<>(Arrays.asList(TEXT));
        float sliderOffset;

        // General Settings
        makeDataViewer();
        makeHeader(getNextText());
        // Enable mod
        makeToggler(getNextText(), ChimeraMonstersConfig.BoolSetting.ENABLE_MOD);
        // Enable shaders
        makeToggler(getNextText(), ChimeraMonstersConfig.BoolSetting.ENABLE_SHADERS);
        // Enable tips
        makeToggler(getNextText(), ChimeraMonstersConfig.BoolSetting.ENABLE_TOOLTIPS);

        // Monster Settings
        makePageBreak();
        makeHeader(getNextText());
        sliderOffset = getSliderPosition(labelStrings.subList(textIndex, textIndex + 5));
        // Percent chance to apply modifier
        makeSlider(getNextText(), ChimeraMonstersConfig.IntSetting.MOD_CHANCE, sliderOffset, 0, 100);
        // Roll amount
        makeSlider(getNextText(), ChimeraMonstersConfig.IntSetting.ROLL_ATTEMPTS, sliderOffset, 1, 3);
        // Common mod weight
        makeSlider(getNextText(), ChimeraMonstersConfig.IntSetting.COMMON_WEIGHT, sliderOffset, 0, 10);
        // Uncommon mod weight
        makeSlider(getNextText(), ChimeraMonstersConfig.IntSetting.UNCOMMON_WEIGHT, sliderOffset, 0, 10);
        // Rare mod weight
        makeSlider(getNextText(), ChimeraMonstersConfig.IntSetting.RARE_WEIGHT, sliderOffset, 0, 10);
        // Rarity bias
        makeSlider(getNextText(), ChimeraMonstersConfig.IntSetting.RARITY_BIAS, sliderOffset, 0, 10);

        // Fight Settings
        makePageBreak();
        makeHeader(getNextText());
        sliderOffset = getSliderPosition(labelStrings.subList(textIndex, textIndex + 3));
        // Enhanced weight
        makeSlider(getNextText(), ChimeraMonstersConfig.IntSetting.ENHANCED_WEIGHT, sliderOffset, 0, 10);
        // Themed weight
        makeSlider(getNextText(), ChimeraMonstersConfig.IntSetting.THEMED_WEIGHT, sliderOffset, 0, 10);
        // Curated weight
        makeSlider(getNextText(), ChimeraMonstersConfig.IntSetting.CURATED_WEIGHT, sliderOffset, 0, 10);
        // Vanilla weight
        makeSlider(getNextText(), ChimeraMonstersConfig.IntSetting.VANILLA_WEIGHT, sliderOffset, 0, 10);



        // Menu control
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

        ChimeraMonstersMod.logger.info("Done loading badge Image and mod options");

        // New section for disabling entire crossover content
        makePageBreak();
        ChimeraMonstersMod.onSetupSettingsPanel(crossoverUIStrings.TEXT[0]);
    }

    public static String getNextText() {
        return TEXT[textIndex++];
    }

    public static void makeHeader(String text) {
        registerUIElement(new CenteredModLabel(text, Settings.WIDTH/2f/Settings.xScale, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {}));
    }

    public static void makeDataViewer() {
        ModLabeledToggleTooltipButton dataButton = new ModLabeledToggleTooltipButton(TEXT[0], getProbabilityData(), LAYOUT_X + 830f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                ChimeraMonstersConfig.BoolSetting.SHOW_BREAKDOWN.getVal(), settingsPanel, panel -> panel.tooltip = getProbabilityData(), (button) -> ChimeraMonstersConfig.BoolSetting.SHOW_BREAKDOWN.setVal(button.enabled));
        settingsPanel.addUIElement(dataButton);
    }

    public static void makeToggler(String text, ChimeraMonstersConfig.BoolSetting setting) {
        registerUIElement(new ModLabeledToggleButton(text,LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                setting.getVal(), settingsPanel, (label) -> {}, (button) -> setting.setVal(button.enabled)));
    }

    public static void makeSlider(String text, ChimeraMonstersConfig.IntSetting setting, float sliderXOffset, int min, int max) {
        registerUIElement(new ModLabel(text, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR,
                        FontHelper.charDescFont, settingsPanel, modLabel -> {}), false);
        registerUIElement(new ModMinMaxSlider("", LAYOUT_X + sliderXOffset, LAYOUT_Y + 7f,
                min, max, setting.getVal(), "%.0f", settingsPanel,
                slider -> setting.setVal(Math.round(slider.getValue()))));
    }

    public static void makeModToggler(String modID, String labelText) {
        ModLabeledToggleButton enableCrossoverButton = new ModLabeledToggleButton(labelText, LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                ChimeraMonstersConfig.chimeraMonstersCrossoverConfig.getBool(modID), settingsPanel,
                (label) -> {
                    label.text = crossoverLabelMap.get(modID) + " (" + ChimeraMonstersMod.crossoverSizeMap.get(modID) + " " + crossoverUIStrings.TEXT[2] + ")";
                },
                (button) -> {
                    ChimeraMonstersConfig.chimeraMonstersCrossoverConfig.setBool(modID, button.enabled);
                    ChimeraMonstersConfig.crossoverEnableMap.put(modID, button.enabled);
                    try {ChimeraMonstersConfig.chimeraMonstersCrossoverConfig.save();} catch (IOException e) {e.printStackTrace();}
                });
        registerUIElement(enableCrossoverButton);
    }

    public static void makeLineBreak() {
        currentY += SPACING_Y;
    }

    public static void makePageBreak() {
        currentY = 0;
        pages.put(pages.size(), new ArrayList<>());
    }

    private static void registerUIElement(IUIElement elem) {
        registerUIElement(elem, true);
    }

    private static void registerUIElement(IUIElement elem, boolean decrement) {
        settingsPanel.addUIElement(elem);
        if (pages.isEmpty()) {
            pages.put(0, new ArrayList<>());
        }
        if (currentY > FULL_PAGE_Y) {
            makePageBreak();
        }
        int page = pages.size()-1;
        pages.get(page).add(elem);
        elem.setY(elem.getY() - currentY);
        elem.setX(elem.getX() + (page * Settings.WIDTH)/Settings.scale);
        //elem.setY((elem.getY() - deltaY)/Settings.scale*Settings.yScale);
        //elem.setX((elem.getX()*Settings.xScale + (page * Settings.WIDTH))/Settings.scale);
        if (decrement) {
            makeLineBreak();
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
        int percent = ChimeraMonstersConfig.IntSetting.MOD_CHANCE.getVal();
        int rolls = ChimeraMonstersConfig.IntSetting.ROLL_ATTEMPTS.getVal();
        return (float) ((Math.pow(percent/100f, exactly) * Math.pow(1-percent/100f, rolls-exactly)) * 100f * combination(rolls, exactly));
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
        int common = ChimeraMonstersConfig.IntSetting.COMMON_WEIGHT.getVal();
        int uncommon = ChimeraMonstersConfig.IntSetting.UNCOMMON_WEIGHT.getVal();
        int rare = ChimeraMonstersConfig.IntSetting.RARE_WEIGHT.getVal();
        int bias = ChimeraMonstersConfig.IntSetting.RARITY_BIAS.getVal();
        int total = common + uncommon + rare + bias;
        if (total == 0) {
            return 0;
        }
        switch (r) {
            case COMMON:
                return 100 * ((float) common + (matches ? bias : 0)) / total;
            case UNCOMMON:
                return 100 * ((float) uncommon + (matches ? bias : 0)) / total;
            case RARE:
                return 100 * ((float) rare + (matches ? bias : 0)) / total;
            case SPECIAL:
                return 0;
        }
        return 0;
    }

    private static float getWeightProbability(AbstractMonsterModifier.ModifierRarity r) {
        int common = ChimeraMonstersConfig.IntSetting.COMMON_WEIGHT.getVal();
        int uncommon = ChimeraMonstersConfig.IntSetting.UNCOMMON_WEIGHT.getVal();
        int rare = ChimeraMonstersConfig.IntSetting.RARE_WEIGHT.getVal();
        int total = common + uncommon + rare;
        if (total == 0) {
            return 0;
        }
        switch (r) {
            case COMMON:
                return 100 * ((float) common) / total;
            case UNCOMMON:
                return 100 * ((float) uncommon) / total;
            case RARE:
                return 100 * ((float) rare) / total;
            case SPECIAL:
                return 0;
        }
        return 0;
    }

    private static String getProbabilityData() {
        StringBuilder sb = new StringBuilder();
        sb.append(EXTRA_TEXT[2]);
        int enhanced = ChimeraMonstersConfig.IntSetting.ENHANCED_WEIGHT.getVal();
        int themed = ChimeraMonstersConfig.IntSetting.THEMED_WEIGHT.getVal();
        int curated = ChimeraMonstersConfig.IntSetting.CURATED_WEIGHT.getVal();
        int vanilla = ChimeraMonstersConfig.IntSetting.VANILLA_WEIGHT.getVal();
        if (enhanced + themed + curated > 0) {
            int fightSum = enhanced + themed + curated + vanilla;
            if (enhanced > 0) {
                sb.append(EXTRA_TEXT[13]).append(String.format("%.02f", 100f * enhanced / fightSum)).append("%");
            }
            if (themed > 0) {
                sb.append(EXTRA_TEXT[12]).append(String.format("%.02f", 100f * themed / fightSum)).append("%");
            }
            if (curated > 0) {
                sb.append(EXTRA_TEXT[11]).append(String.format("%.02f", 100f * curated / fightSum)).append("%");
            }
            if (vanilla > 0) {
                sb.append(EXTRA_TEXT[14]).append(String.format("%.02f", 100f * vanilla / fightSum)).append("%");
            }
            sb.append(EXTRA_TEXT[15]);
            for (int i = 0; i <= ChimeraMonstersConfig.IntSetting.ROLL_ATTEMPTS.getVal() ; i++) {
                float chance = getRollProbability(i);
                if (chance > 0f) {
                    sb.append(" NL #b").append(i).append(EXTRA_TEXT[i == 1 ? 3 : 4]).append(String.format("%.02f", chance)).append("%");
                }
            }
            if (ChimeraMonstersConfig.IntSetting.MOD_CHANCE.getVal() > 0f) {
                if (ChimeraMonstersConfig.IntSetting.RARITY_BIAS.getVal() == 0) {
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
            }
        } else {
            sb.append(EXTRA_TEXT[14]).append(String.format("%.02f", 100f)).append("%");
        }
        return sb.toString();
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
