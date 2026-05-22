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
import java.util.function.Consumer;

public class ChimeraMonstersSettingsPanel {
    public static ModPanel settingsPanel;
    public static UIStrings uiStrings;
    public static UIStrings crossoverUIStrings;
    public static String[] TEXT;
    public static String[] EXTRA_TEXT;
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

        //Get the longest slider text for positioning
        ArrayList<String> labelStrings = new ArrayList<>(Arrays.asList(TEXT));
        float sliderOffset = getSliderPosition(labelStrings.subList(1,5));
        labelStrings.clear();

        //Show data?
        makeDataViewer();
        /*ModLabeledToggleTooltipButton dataButton = new ModLabeledToggleTooltipButton(SHOW_ANALYSIS_TEXT, getProbabilityData(), LAYOUT_X + 830f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                ChimeraMonstersConfig.chimeraMonstersConfig.getBool(ChimeraMonstersConfig.SHOW_BREAKDOWN), settingsPanel, panel -> panel.tooltip = getProbabilityData(), (button) -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setBool(ChimeraMonstersConfig.SHOW_BREAKDOWN, button.enabled);
            ChimeraMonstersConfig.showBreakdown = button.enabled;
            try {
                ChimeraMonstersConfig.chimeraMonstersConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        registerUIElement(dataButton, false);*/

        //Enable or disable the mod entirely.
        makeToggler(ENABLE_TEXT, ChimeraMonstersConfig.ENABLE_MODS_SETTING, b -> ChimeraMonstersConfig.enableMods = b);
        /*ModLabeledToggleButton enableModsButton = new ModLabeledToggleButton(ENABLE_TEXT,LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                ChimeraMonstersConfig.chimeraMonstersConfig.getBool(ChimeraMonstersConfig.ENABLE_MODS_SETTING), settingsPanel, (label) -> {}, (button) -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setBool(ChimeraMonstersConfig.ENABLE_MODS_SETTING, button.enabled);
            ChimeraMonstersConfig.enableMods = button.enabled;
            try {
                ChimeraMonstersConfig.chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });*/

        //Used for probability of a mod being applied
        ModLabel probabilityLabel = new ModLabel(MOD_CHANCE_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider probabilitySlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 100, ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.MOD_PROBABILITY), "%.0f", settingsPanel, slider -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setInt(ChimeraMonstersConfig.MOD_PROBABILITY, Math.round(slider.getValue()));
            ChimeraMonstersConfig.modProbabilityPercent = Math.round(slider.getValue());
            try {
                ChimeraMonstersConfig.chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for roll attempts
        ModLabel attemptsLabel = new ModLabel(ROLL_ATTEMPTS_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider attemptsSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                1, 3, ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.ROLL_ATTEMPTS), "%.0f", settingsPanel, slider -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setInt(ChimeraMonstersConfig.ROLL_ATTEMPTS, Math.round(slider.getValue()));
            ChimeraMonstersConfig.rollAttempts = Math.round(slider.getValue());
            try {
                ChimeraMonstersConfig.chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for modified fight weight
        ModLabel modifiedFightLabel = new ModLabel(MODIFIED_WEIGHT_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider modifiedFightSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 10, ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.MODIFIED_FIGHT_WEIGHT), "%.0f", settingsPanel, slider -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setInt(ChimeraMonstersConfig.MODIFIED_FIGHT_WEIGHT, Math.round(slider.getValue()));
            ChimeraMonstersConfig.modifiedFightWeight = Math.round(slider.getValue());
            try {
                ChimeraMonstersConfig.chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        makeSlider(MODIFIED_WEIGHT_TEXT, ChimeraMonstersConfig.MODIFIED_FIGHT_WEIGHT, sliderOffset, 0, 10, i -> ChimeraMonstersConfig.modifiedFightWeight = i);

        //Used for thematic fight weight
        ModLabel thematicFightLabel = new ModLabel(THEMATIC_WEIGHT_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider thematicFightSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 10, ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.THEMED_FIGHT_WEIGHT), "%.0f", settingsPanel, slider -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setInt(ChimeraMonstersConfig.THEMED_FIGHT_WEIGHT, Math.round(slider.getValue()));
            ChimeraMonstersConfig.themedFightWeight = Math.round(slider.getValue());
            try {
                ChimeraMonstersConfig.chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for curated fight weight
        ModLabel deviantFightLabel = new ModLabel(DEVIANT_WEIGHT_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider deviantFightSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 10, ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.CURATED_FIGHT_WEIGHT), "%.0f", settingsPanel, slider -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setInt(ChimeraMonstersConfig.CURATED_FIGHT_WEIGHT, Math.round(slider.getValue()));
            ChimeraMonstersConfig.curatedFightWeight = Math.round(slider.getValue());
            try {
                ChimeraMonstersConfig.chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for unmodified fight weight
        ModLabel unmodifiedFightLabel = new ModLabel(UNMODIFIED_WEIGHT_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider unmodifiedFightSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 10, ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.UNMODIFIED_FIGHT_WEIGHT), "%.0f", settingsPanel, slider -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setInt(ChimeraMonstersConfig.UNMODIFIED_FIGHT_WEIGHT, Math.round(slider.getValue()));
            ChimeraMonstersConfig.unmodifiedFightWeight = Math.round(slider.getValue());
            try {
                ChimeraMonstersConfig.chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for common mod weight
        ModLabel commonLabel = new ModLabel(COMMON_WEIGHT_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider commonSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 10, ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.COMMON_WEIGHT), "%.0f", settingsPanel, slider -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setInt(ChimeraMonstersConfig.COMMON_WEIGHT, Math.round(slider.getValue()));
            ChimeraMonstersConfig.commonWeight = Math.round(slider.getValue());
            try {
                ChimeraMonstersConfig.chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for uncommon mod weight
        ModLabel uncommonLabel = new ModLabel(UNCOMMON_WEIGHT_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider uncommonSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 10, ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.UNCOMMON_WEIGHT), "%.0f", settingsPanel, slider -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setInt(ChimeraMonstersConfig.UNCOMMON_WEIGHT, Math.round(slider.getValue()));
            ChimeraMonstersConfig.uncommonWeight = Math.round(slider.getValue());
            try {
                ChimeraMonstersConfig.chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for rare mod weight
        ModLabel rareLabel = new ModLabel(RARE_WEIGHT_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider rareSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 10, ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.RARE_WEIGHT), "%.0f", settingsPanel, slider -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setInt(ChimeraMonstersConfig.RARE_WEIGHT, Math.round(slider.getValue()));
            ChimeraMonstersConfig.rareWeight = Math.round(slider.getValue());
            try {
                ChimeraMonstersConfig.chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used for bias weight
        ModLabel biasLabel = new ModLabel(WEIGHT_BIAS_TEXT, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider biasSlider = new ModMinMaxSlider("",
                LAYOUT_X + sliderOffset,
                LAYOUT_Y + 7f,
                0, 5, ChimeraMonstersConfig.chimeraMonstersConfig.getInt(ChimeraMonstersConfig.RARITY_BIAS), "%.0f", settingsPanel, slider -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setInt(ChimeraMonstersConfig.RARITY_BIAS, Math.round(slider.getValue()));
            ChimeraMonstersConfig.rarityBias = Math.round(slider.getValue());
            try {
                ChimeraMonstersConfig.chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used enable tooltips
        ModLabeledToggleButton enableTooltipsButton = new ModLabeledToggleButton(ENABLE_TIPS_TEXT,LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                ChimeraMonstersConfig.chimeraMonstersConfig.getBool(ChimeraMonstersConfig.ENABLE_TOOLTIPS), settingsPanel, (label) -> {}, (button) -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setBool(ChimeraMonstersConfig.ENABLE_TOOLTIPS, button.enabled);
            ChimeraMonstersConfig.enableTooltips = button.enabled;
            try {
                ChimeraMonstersConfig.chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });

        //Used enable shaders
        ModLabeledToggleButton enableShadersButton = new ModLabeledToggleButton(ENABLE_SHADERS_TEXT,LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                ChimeraMonstersConfig.chimeraMonstersConfig.getBool(ChimeraMonstersConfig.ENABLE_SHADERS), settingsPanel, (label) -> {}, (button) -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setBool(ChimeraMonstersConfig.ENABLE_SHADERS, button.enabled);
            ChimeraMonstersConfig.enableTooltips = button.enabled;
            try {
                ChimeraMonstersConfig.chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        });


        //registerUIElement(enableModsButton);
        //registerUIElement(modifiedFightLabel, false);
        //registerUIElement(modifiedFightSlider);
        registerUIElement(thematicFightLabel, false);
        registerUIElement(thematicFightSlider);
        registerUIElement(deviantFightLabel, false);
        registerUIElement(deviantFightSlider);
        registerUIElement(unmodifiedFightLabel, false);
        registerUIElement(unmodifiedFightSlider);
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

        ChimeraMonstersMod.logger.info("Done loading badge Image and mod options");
        ChimeraMonstersMod.onSetupSettingsPanel(crossoverUIStrings.TEXT[0]);
    }

    public static void makeHeader(String text) {
        registerUIElement(new CenteredModLabel(text, Settings.WIDTH/2f/Settings.xScale, LAYOUT_Y, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {}));
    }

    public static void makeDataViewer() {
        ModLabeledToggleTooltipButton dataButton = new ModLabeledToggleTooltipButton(TEXT[8], getProbabilityData(), LAYOUT_X + 830f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                ChimeraMonstersConfig.chimeraMonstersConfig.getBool(ChimeraMonstersConfig.SHOW_BREAKDOWN), settingsPanel, panel -> panel.tooltip = getProbabilityData(), (button) -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setBool(ChimeraMonstersConfig.SHOW_BREAKDOWN, button.enabled);
            ChimeraMonstersConfig.showBreakdown = button.enabled;
            try {
                ChimeraMonstersConfig.chimeraMonstersConfig.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        registerUIElement(dataButton, false);
    }

    public static void makeToggler(String text, String key, Consumer<Boolean> callback) {
        registerUIElement(new ModLabeledToggleButton(text,LAYOUT_X - 40f, LAYOUT_Y - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                ChimeraMonstersConfig.chimeraMonstersConfig.getBool(key), settingsPanel, (label) -> {}, (button) -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setBool(key, button.enabled);
            callback.accept(button.enabled);
            try {ChimeraMonstersConfig.chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        }));
    }

    public static void makeSlider(String text, String key, float sliderXOffset, int min, int max, Consumer<Integer> callback) {
        registerUIElement(new ModLabel(text, LAYOUT_X, LAYOUT_Y, Settings.CREAM_COLOR,
                        FontHelper.charDescFont, settingsPanel, modLabel -> {}), false);
        registerUIElement(new ModMinMaxSlider("",
                LAYOUT_X + sliderXOffset,
                LAYOUT_Y + 7f,
                min, max, ChimeraMonstersConfig.chimeraMonstersConfig.getInt(key), "%.0f", settingsPanel, slider -> {
            ChimeraMonstersConfig.chimeraMonstersConfig.setInt(key, Math.round(slider.getValue()));
            callback.accept(Math.round(slider.getValue()));
            try {ChimeraMonstersConfig.chimeraMonstersConfig.save();} catch (IOException e) {e.printStackTrace();}
        }));
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
        return (float) ((Math.pow(ChimeraMonstersConfig.modProbabilityPercent/100f, exactly) * Math.pow(1- ChimeraMonstersConfig.modProbabilityPercent/100f, ChimeraMonstersConfig.rollAttempts-exactly)) * 100f * combination(ChimeraMonstersConfig.rollAttempts, exactly));
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
        if (ChimeraMonstersConfig.commonWeight + ChimeraMonstersConfig.uncommonWeight + ChimeraMonstersConfig.rareWeight + ChimeraMonstersConfig.rarityBias == 0) {
            return 0;
        }
        switch (r) {
            case COMMON:
                return 100 * ((float) ChimeraMonstersConfig.commonWeight + (matches ? ChimeraMonstersConfig.rarityBias : 0)) / (ChimeraMonstersConfig.commonWeight + ChimeraMonstersConfig.uncommonWeight + ChimeraMonstersConfig.rareWeight + ChimeraMonstersConfig.rarityBias);
            case UNCOMMON:
                return 100 * ((float) ChimeraMonstersConfig.uncommonWeight + (matches ? ChimeraMonstersConfig.rarityBias : 0)) / (ChimeraMonstersConfig.commonWeight + ChimeraMonstersConfig.uncommonWeight + ChimeraMonstersConfig.rareWeight + ChimeraMonstersConfig.rarityBias);
            case RARE:
                return 100 * ((float) ChimeraMonstersConfig.rareWeight + (matches ? ChimeraMonstersConfig.rarityBias : 0)) / (ChimeraMonstersConfig.commonWeight + ChimeraMonstersConfig.uncommonWeight + ChimeraMonstersConfig.rareWeight + ChimeraMonstersConfig.rarityBias);
            case SPECIAL:
                return 0;
        }
        return 0;
    }

    private static float getWeightProbability(AbstractMonsterModifier.ModifierRarity r) {
        if (ChimeraMonstersConfig.commonWeight + ChimeraMonstersConfig.uncommonWeight + ChimeraMonstersConfig.rareWeight == 0) {
            return 0;
        }
        switch (r) {
            case COMMON:
                return 100 * ((float) ChimeraMonstersConfig.commonWeight) / (ChimeraMonstersConfig.commonWeight + ChimeraMonstersConfig.uncommonWeight + ChimeraMonstersConfig.rareWeight);
            case UNCOMMON:
                return 100 * ((float) ChimeraMonstersConfig.uncommonWeight) / (ChimeraMonstersConfig.commonWeight + ChimeraMonstersConfig.uncommonWeight + ChimeraMonstersConfig.rareWeight);
            case RARE:
                return 100 * ((float) ChimeraMonstersConfig.rareWeight) / (ChimeraMonstersConfig.commonWeight + ChimeraMonstersConfig.uncommonWeight + ChimeraMonstersConfig.rareWeight);
            case SPECIAL:
                return 0;
        }
        return 0;
    }

    private static String getProbabilityData() {
        StringBuilder sb = new StringBuilder();
        sb.append(EXTRA_TEXT[2]);
        if (ChimeraMonstersConfig.modifiedFightWeight + ChimeraMonstersConfig.themedFightWeight + ChimeraMonstersConfig.curatedFightWeight > 0) {
            int fightSum = ChimeraMonstersConfig.modifiedFightWeight + ChimeraMonstersConfig.themedFightWeight + ChimeraMonstersConfig.curatedFightWeight + ChimeraMonstersConfig.unmodifiedFightWeight;
            if (ChimeraMonstersConfig.modifiedFightWeight > 0) {
                sb.append(EXTRA_TEXT[13]).append(String.format("%.02f", 100f * ChimeraMonstersConfig.modifiedFightWeight / fightSum)).append("%");
            }
            if (ChimeraMonstersConfig.themedFightWeight > 0) {
                sb.append(EXTRA_TEXT[12]).append(String.format("%.02f", 100f * ChimeraMonstersConfig.themedFightWeight / fightSum)).append("%");
            }
            if (ChimeraMonstersConfig.curatedFightWeight > 0) {
                sb.append(EXTRA_TEXT[11]).append(String.format("%.02f", 100f * ChimeraMonstersConfig.curatedFightWeight / fightSum)).append("%");
            }
            if (ChimeraMonstersConfig.unmodifiedFightWeight > 0) {
                sb.append(EXTRA_TEXT[14]).append(String.format("%.02f", 100f * ChimeraMonstersConfig.unmodifiedFightWeight / fightSum)).append("%");
            }
            sb.append(EXTRA_TEXT[15]);
            for (int i = 0; i <= ChimeraMonstersConfig.rollAttempts ; i++) {
                float chance = getRollProbability(i);
                if (chance > 0f) {
                    sb.append(" NL #b").append(i).append(EXTRA_TEXT[i == 1 ? 3 : 4]).append(String.format("%.02f", chance)).append("%");
                }
            }
            if (ChimeraMonstersConfig.modProbabilityPercent > 0f) {
                if (ChimeraMonstersConfig.rarityBias == 0) {
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
