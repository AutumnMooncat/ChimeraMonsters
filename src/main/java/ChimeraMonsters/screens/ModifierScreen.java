package ChimeraMonsters.screens;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.cards.MonsterCard;
import ChimeraMonsters.commands.Monster;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.patches.MainMenuPatches;
import ChimeraMonsters.ui.SettingsButton;
import basemod.BaseMod;
import basemod.ModBadge;
import basemod.ReflectionHacks;
import basemod.patches.com.megacrit.cardcrawl.screens.options.DropdownMenu.DropdownColoring;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import com.megacrit.cardcrawl.screens.options.DropdownMenuListener;
import javassist.CtBehavior;

import java.lang.reflect.Method;
import java.util.*;

public class ModifierScreen implements DropdownMenuListener, ScrollBarListener {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID("ModifierScreen"));
    public static final String[] TEXT = uiStrings.TEXT;
    private static final int CARDS_PER_LINE = (int)((float)Settings.WIDTH / (AbstractCard.IMG_WIDTH * 0.75F + Settings.CARD_VIEW_PAD_X * 3.0F));
    private static final float DROPDOWN_X = 20f * Settings.scale;
    private static final float MOD_DROPDOWN_Y = Settings.HEIGHT/2f + 340.0F * Settings.scale;
    private static final float RARITY_DROPDOWN_Y = MOD_DROPDOWN_Y - 60F * Settings.scale;
    private static final float AUGMENT_DROPDOWN_Y = RARITY_DROPDOWN_Y - 60F * Settings.scale;
    private static final float CHARACTER_DROPDOWN_Y = AUGMENT_DROPDOWN_Y - 60F * Settings.scale;
    private static final float RARITY_Y = CHARACTER_DROPDOWN_Y - 50f * Settings.scale;
    private static final float VALID_CARDS_Y = RARITY_Y - 50f * Settings.scale;
    private static final float HB_X = DROPDOWN_X + 100f * Settings.scale;
    private static final float HB_Y = VALID_CARDS_Y - 60f * Settings.scale;
    private static final float DISABLE_Y = HB_Y - 60f * Settings.scale;
    private static float drawStartX;
    private static final float drawStartY = (float)Settings.HEIGHT * 0.8F; //0.66
    private static final float padX = AbstractCard.IMG_WIDTH * 0.75F + Settings.CARD_VIEW_PAD_X;
    private static final float padY = AbstractCard.IMG_HEIGHT * 0.75F + Settings.CARD_VIEW_PAD_Y;
    private static final Color DISABLE_COLOR = Settings.RED_TEXT_COLOR.cpy();
    private boolean grabbedScreen = false;
    private float grabStartY = 0.0F;
    private float currentDiffY = 0.0F;
    private float scrollLowerBound;
    private float scrollUpperBound;
    private boolean justSorted;
    private final HashMap<String, AbstractMonsterModifier> augmentMap = new HashMap<>();
    private AbstractMonsterModifier selectedAugment;
    private String selectedModID;
    private final CardGroup validCards;
    private final CardGroup cardsToRender;
    private AbstractCard hoveredCard;
    private AbstractCard clickStartedCard;
    private final MenuCancelButton cancelButton;
    private final SettingsButton settingsButton;
    private DropdownMenu modDropdown;
    private DropdownMenu rarityDropdown;
    private DropdownMenu augmentDropdown;
    private DropdownMenu characterDropdown;
    private AbstractMonsterModifier.ModifierRarity rarityFilter;
    private String modIDFilter;
    private HashMap<String, AbstractMonsterModifier.ModifierRarity> rarityMap = new HashMap<>();
    private HashMap<String, String> modIDMap = new HashMap<>();
    private ScrollBar scrollBar;
    private Hitbox upgradeHb;
    private Hitbox disableHb;
    private boolean upgradePreview;
    private boolean modifierDisabled;
    private boolean ignoreScrollReset;
    private static ModBadge myBadge;
    private static Class<?> dropdownRowClass;
    private static AbstractCard fallback;
    
    public ModifierScreen() {
        fallback = new Madness();
        upgradeHb = new Hitbox(250.0F * Settings.scale, 60.0F * Settings.scale);
        disableHb = new Hitbox(250.0F * Settings.scale, 60.0F * Settings.scale);
        cancelButton = new MenuCancelButton();
        settingsButton = new SettingsButton();

        validCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        cardsToRender = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

        modDropdown = new DropdownMenu(this, getModStrings(), FontHelper.tipBodyFont, Settings.CREAM_COLOR);
        rarityDropdown = new DropdownMenu(this, getRarityStrings(), FontHelper.tipBodyFont, Settings.CREAM_COLOR);
        augmentDropdown = new DropdownMenu(this, getModifierStrings(), FontHelper.tipBodyFont, Settings.CREAM_COLOR);
        characterDropdown = new DropdownMenu(this, getModNameStrings(), FontHelper.tipBodyFont, Settings.CREAM_COLOR);

        scrollBar = new ScrollBar(this);
        scrollLowerBound = -Settings.DEFAULT_SCROLL_LIMIT;// 47
        scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;// 48

        drawStartX = (float)Settings.WIDTH;// 71
        drawStartX -= (float)(CARDS_PER_LINE - 0) * AbstractCard.IMG_WIDTH * 0.75F;// 72
        drawStartX -= (float)(CARDS_PER_LINE - 1) * Settings.CARD_VIEW_PAD_X;// 73
        drawStartX /= 2.0F;// 74
        drawStartX += AbstractCard.IMG_WIDTH * 0.75F / 2.0F;// 75
        refreshDropdownMenu(modDropdown);
    }
    
    public void open() {
        SingleCardViewPopup.isViewingUpgrade = false;
        upgradePreview = false;
        cancelButton.show(TEXT[0]);
        settingsButton.show(TEXT[5]);
        CardCrawlGame.mainMenuScreen.screen = MainMenuPatches.Enums.MONSTER_MODIFIERS_VIEW; //This is how we tell it what screen is open
        CardCrawlGame.mainMenuScreen.darken();
        upgradeHb.move(HB_X, HB_Y);
        disableHb.move(HB_X, DISABLE_Y);
    }

    public void update() {
        if (modDropdown.isOpen) {
            modDropdown.update();
        } else if (rarityDropdown.isOpen) {
            rarityDropdown.update();
        } else if (augmentDropdown.isOpen) {
            augmentDropdown.update();
        } else if (characterDropdown.isOpen) {
            characterDropdown.update();
        } else {
            updateButtons();
            boolean isScrollBarScrolling = scrollBar.update();// 186
            if (!CardCrawlGame.cardPopup.isOpen && !isScrollBarScrolling) {// 187
                updateScrolling();// 188
            }
            this.upgradeHb.update();// 231
            if (this.upgradeHb.hovered && InputHelper.justClickedLeft) {// 233
                this.upgradeHb.clickStarted = true;// 234
            }

            if (this.upgradeHb.clicked || CInputActionSet.proceed.isJustPressed()) {// 237
                CInputActionSet.proceed.unpress();// 238
                this.upgradeHb.clicked = false;// 239
                upgradePreview = !upgradePreview;// 240
                ignoreScrollReset = true;
                refreshDropdownMenu(augmentDropdown);
            }

            if (selectedAugment != null && selectedAugment.getModRarity() != AbstractMonsterModifier.ModifierRarity.SPECIAL) {
                this.disableHb.update();
                if (this.disableHb.hovered && InputHelper.justClickedLeft) {// 233
                    this.disableHb.clickStarted = true;// 234
                }

                if (this.disableHb.clicked || CInputActionSet.proceed.isJustPressed()) {// 237
                    CInputActionSet.proceed.unpress();// 238
                    this.disableHb.clicked = false;// 239
                    modifierDisabled = !modifierDisabled;
                    ChimeraMonstersMod.setModifierStatus(selectedAugment, modifierDisabled);
                }
            }

            updateCards();
            modDropdown.update();
            rarityDropdown.update();
            augmentDropdown.update();
            characterDropdown.update();
            if (this.hoveredCard != null) {// 166
                CardCrawlGame.cursor.changeType(GameCursor.CursorType.INSPECT);// 167
                if (InputHelper.justClickedLeft) {// 168
                    this.clickStartedCard = this.hoveredCard;// 169
                }

                if (InputHelper.justReleasedClickLeft && this.clickStartedCard != null && this.hoveredCard != null || this.hoveredCard != null && CInputActionSet.select.isJustPressed()) {// 171 172
                    if (Settings.isControllerMode) {// 174
                        this.clickStartedCard = this.hoveredCard;// 175
                    }

                    InputHelper.justReleasedClickLeft = false;// 178
                    CardCrawlGame.cardPopup.open(this.clickStartedCard, cardsToRender);// 179
                    this.clickStartedCard = null;// 180
                }
            } else {
                this.clickStartedCard = null;// 183
            }
        }
    }

    public void updateButtons() {
        cancelButton.update();
        if (cancelButton.hb.clicked || InputHelper.pressedEscape) {
            CardCrawlGame.mainMenuScreen.superDarken = false;
            InputHelper.pressedEscape = false;
            cancelButton.hb.clicked = false;
            cancelButton.hide();
            settingsButton.hide();
            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
            CardCrawlGame.mainMenuScreen.lighten();
        }

        settingsButton.update();
        if (settingsButton.hb.clicked) {
            settingsButton.hb.clicked = false;
            cancelButton.hide();
            settingsButton.hide();
            try {
                Class<?> ModsScreen = Class.forName("com.evacipated.cardcrawl.modthespire.patches.modsscreen.ModsScreen");
                Class<?> ModMenuButton = Class.forName("com.evacipated.cardcrawl.modthespire.patches.modsscreen.ModMenuButton");
                Object o = ReflectionHacks.getPrivateStatic(ModMenuButton, "modsScreen");
                if (o == null) {
                    o = ModsScreen.getConstructor().newInstance();
                    ReflectionHacks.setPrivateStatic(ModMenuButton, "modsScreen", o);
                }
                Method open = ModsScreen.getDeclaredMethod("open");
                open.invoke(o);
                for (int i = 0 ; i < Loader.MODINFOS.length ; i++) {
                    ModInfo info = Loader.MODINFOS[i];
                    if (info.ID.equals(ChimeraMonstersMod.getModID())) {
                        ReflectionHacks.setPrivate(o, ModsScreen, "selectedMod", i);
                        break;
                    }
                }
                if (myBadge != null) {
                    ReflectionHacks.RMethod mboc = ReflectionHacks.privateMethod(ModsScreen, "modBadge_onClick", Object.class);
                    mboc.invoke(o, myBadge);
                }
            } catch (Exception ignored) {
                CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
                CardCrawlGame.mainMenuScreen.lighten();
            }
        }
    }

    public void render(SpriteBatch sb) {
        scrollBar.render(sb);
        cancelButton.render(sb);
        settingsButton.render(sb);
        if (selectedAugment != null) {
            renderUpgradeViewToggle(sb);
            renderInfo(sb);
        }
        characterDropdown.render(sb, DROPDOWN_X, CHARACTER_DROPDOWN_Y);
        augmentDropdown.render(sb, DROPDOWN_X, AUGMENT_DROPDOWN_Y);
        rarityDropdown.render(sb, DROPDOWN_X, RARITY_DROPDOWN_Y);
        modDropdown.render(sb, DROPDOWN_X, MOD_DROPDOWN_Y);
        renderCards(sb);
    }

    public ArrayList<String> getModStrings() {
        ArrayList<String> ret = new ArrayList<>();
        for (AbstractMonsterModifier a : ChimeraMonstersMod.crossoverMap.keySet()) {
            String s = ChimeraMonstersMod.crossoverMap.get(a);
            if (!ret.contains(s)) {
                ret.add(s);
            }
        }
        Collections.sort(ret);
        return ret;
    }

    public ArrayList<String> getRarityStrings() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(TEXT[6]);
        rarityMap.put(TEXT[6], null);
        for (AbstractMonsterModifier.ModifierRarity r : AbstractMonsterModifier.ModifierRarity.values()) {
            ret.add(r.toString());
            rarityMap.put(r.toString(), r);
        }
        return ret;
    }

    public ArrayList<String> getModifierStrings() {
        ArrayList<String> ret = new ArrayList<>();
        augmentMap.clear();
        for (String id : ChimeraMonstersMod.modMap.keySet()) {
            AbstractMonsterModifier mod = ChimeraMonstersMod.modMap.get(id);
            if (rarityFilter == null || mod.getModRarity() == rarityFilter) {
                if (ChimeraMonstersMod.crossoverMap.get(mod).equals(selectedModID)) {
                    String s = (mod.getPrefix() + mod.getSuffix()).replace("  ", " ").trim();
                    if (s.isEmpty()) {
                        s = formatText(s);
                    }
                    ret.add(s);
                    augmentMap.put(s, mod);
                }
            }
        }
        ret.sort(String.CASE_INSENSITIVE_ORDER);
        //ret.replaceAll(this::formatText);
        if (ret.isEmpty()) {
            ret.add(TEXT[7]);
        }
        return ret;
    }

    public ArrayList<String> getModNameStrings() {
        ArrayList<String> ret = new ArrayList<>();
        modIDMap.clear();
        ret.add(TEXT[4]);
        modIDMap.put(TEXT[4], null);
        for (Map.Entry<Class<?>, String> entry : ChimeraMonstersMod.modIDMap.entrySet()) {
            if (!ret.contains(entry.getValue())) {
                ret.add(entry.getValue());
            }
        }
        return ret;
    }

    public String formatText(String s) {
        int index = s.lastIndexOf(":");
        if (index != -1) {
            return s.substring(index+1);
        }
        return s;
    }

    @Override
    public void changedSelectionTo(DropdownMenu dropdownMenu, int i, String s) {
        if (dropdownMenu == augmentDropdown) {
            validCards.clear();
            if (s.equals(TEXT[7])) {
                updateCardFilters();
                selectedAugment = null;
                modifierDisabled = false;
                return;
            }
            selectedAugment = augmentMap.get(s);
            modifierDisabled = ChimeraMonstersMod.disabledModifiers.contains(selectedAugment);
            for (Map.Entry<String, AbstractMonster> entry : ChimeraMonstersMod.dummyMonsterMap.entrySet()) {
                if (selectedAugment.canApplyTo(entry.getValue(), null)) {
                    AbstractMonster monster = Monster.createMonster(entry.getKey());
                    if (monster != null) {
                        ChimeraMonstersMod.applyModifier(monster, selectedAugment);
                        AbstractCard card = new MonsterCard(monster);
                        card.targetDrawScale = 0.75f;
                        validCards.addToBottom(card);
                    }
                }
            }
            validCards.sortAlphabetically(true);// 143
            validCards.sortByRarity(true);// 144
            validCards.group.sort(Comparator.comparing(card -> card.color));
            validCards.sortByStatus(true);
            updateCardFilters();
        }
        if (dropdownMenu == modDropdown) {
            selectedModID = s;
            augmentDropdown = new DropdownMenu(this, getModifierStrings(), FontHelper.tipBodyFont, Settings.CREAM_COLOR);
            refreshDropdownMenu(augmentDropdown);
        }
        if (dropdownMenu == characterDropdown) {
            modIDFilter = modIDMap.getOrDefault(s, null);
            updateCardFilters();
        }
        if (dropdownMenu == rarityDropdown) {
            rarityFilter = rarityMap.getOrDefault(s, null);
            augmentDropdown = new DropdownMenu(this, getModifierStrings(), FontHelper.tipBodyFont, Settings.CREAM_COLOR);
            refreshDropdownMenu(augmentDropdown);
        }
    }

    private void updateCardFilters() {
        cardsToRender.clear();
        for (AbstractCard c : validCards.group) {
            if (modIDFilter == null) {
                cardsToRender.addToTop(c);
            } else {
                if (c instanceof MonsterCard) {
                    MonsterCard mc = (MonsterCard) c;
                    if (ChimeraMonstersMod.modIDMap.get(mc.storedMonster.getClass()).equals(modIDFilter)) {
                        cardsToRender.addToTop(c);
                    }
                }
            }
        }
        justSorted = true;
        calculateScrollBounds();
        if (!ignoreScrollReset) {
            currentDiffY = 0;
        }
        ignoreScrollReset = false;
    }

    private void updateCards() {
        this.hoveredCard = null;// 337
        int lineNum = 0;// 338
        ArrayList<AbstractCard> cards = cardsToRender.group;// 340

        for(int i = 0; i < cards.size(); ++i) {// 342
            int mod = i % CARDS_PER_LINE;// 343
            if (mod == 0 && i != 0) {// 344
                ++lineNum;// 345
            }

            cards.get(i).target_x = drawStartX + (float)mod * padX;// 347
            cards.get(i).target_y = drawStartY + this.currentDiffY - (float)lineNum * padY;// 348
            if (justSorted) {// 356
                cards.get(i).current_x = cards.get(i).target_x;
                cards.get(i).current_y = cards.get(i).target_y;
            }
            cards.get(i).update();// 349
            cards.get(i).updateHoverLogic();// 350
            if (cards.get(i).hb.hovered) {// 352
                this.hoveredCard = cards.get(i);// 353
            }
        }

        justSorted = false;
    }

    public void renderCards(SpriteBatch sb) {
        cardsToRender.renderInLibrary(sb);// 503
        cardsToRender.renderTip(sb);
        if (this.hoveredCard != null) {// 426
            this.hoveredCard.renderHoverShadow(sb);// 427
            this.hoveredCard.renderInLibrary(sb);// 428
        }
    }

    private void renderInfo(SpriteBatch sb) {
        FontHelper.renderFont(sb, FontHelper.tipBodyFont, TEXT[2]+selectedAugment.getModRarity().toString(), DROPDOWN_X, RARITY_Y, Settings.GOLD_COLOR);
        FontHelper.renderFont(sb, FontHelper.tipBodyFont, TEXT[3]+cardsToRender.size() + (modIDFilter != null ? "/"+validCards.group.size() : ""), DROPDOWN_X, VALID_CARDS_Y, Settings.GOLD_COLOR);
    }

    private void renderUpgradeViewToggle(SpriteBatch sb) {
        FontHelper.cardTitleFont.getData().setScale(1.0F);
        sb.setColor(Color.WHITE);// 1712
        sb.draw(ImageMaster.CHECKBOX, this.upgradeHb.cX - 80.0F * Settings.scale - 32.0F, this.upgradeHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);// 1713
        if (this.upgradeHb.hovered) {// 1731
            FontHelper.renderFont(sb, FontHelper.tipBodyFont, TEXT[1], this.upgradeHb.cX - 45.0F * Settings.scale, this.upgradeHb.cY + 10.0F * Settings.scale, Settings.BLUE_TEXT_COLOR);// 1732
        } else {
            FontHelper.renderFont(sb, FontHelper.tipBodyFont, TEXT[1], this.upgradeHb.cX - 45.0F * Settings.scale, this.upgradeHb.cY + 10.0F * Settings.scale, Settings.GOLD_COLOR);// 1740
        }

        if (upgradePreview) {// 1749
            sb.setColor(Color.WHITE);// 1750
            sb.draw(ImageMaster.TICK, this.upgradeHb.cX - 80.0F * Settings.scale - 32.0F, this.upgradeHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);// 1751
        }
        this.upgradeHb.render(sb);// 1769

        if (selectedAugment.getModRarity() != AbstractMonsterModifier.ModifierRarity.SPECIAL) {
            sb.draw(ImageMaster.CHECKBOX, this.disableHb.cX - 80.0F * Settings.scale - 32.0F, this.disableHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);// 1713
            if (this.disableHb.hovered) {// 1731
                FontHelper.renderFont(sb, FontHelper.tipBodyFont, TEXT[8], this.disableHb.cX - 45.0F * Settings.scale, this.disableHb.cY + 10.0F * Settings.scale, Settings.RED_TEXT_COLOR);// 1732
            } else {
                FontHelper.renderFont(sb, FontHelper.tipBodyFont, TEXT[8], this.disableHb.cX - 45.0F * Settings.scale, this.disableHb.cY + 10.0F * Settings.scale, Settings.GOLD_COLOR);// 1740
            }

            if (modifierDisabled) {// 1749
                sb.setColor(Color.WHITE);// 1750
                sb.draw(ImageMaster.TICK, this.disableHb.cX - 80.0F * Settings.scale - 32.0F, this.disableHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);// 1751
            }

            this.disableHb.render(sb);
        }
    }

    private void updateScrolling() {
        int y = InputHelper.mY;// 366
        if (!this.grabbedScreen) {// 368
            if (InputHelper.scrolledDown) {// 369
                this.currentDiffY += Settings.SCROLL_SPEED;// 370
            } else if (InputHelper.scrolledUp) {// 371
                this.currentDiffY -= Settings.SCROLL_SPEED;// 372
            }

            if (InputHelper.justClickedLeft) {// 375
                this.grabbedScreen = true;// 376
                this.grabStartY = (float)y - this.currentDiffY;// 377
            }
        } else if (InputHelper.isMouseDown) {// 380
            this.currentDiffY = (float)y - this.grabStartY;// 381
        } else {
            this.grabbedScreen = false;// 383
        }

        this.resetScrolling();// 387
        this.updateBarPosition();// 388
    }// 389

    private void calculateScrollBounds() {
        int size = cardsToRender.size();// 395
        int scrollTmp = 0;// 397
        if (size > CARDS_PER_LINE * 2) {// 398
            scrollTmp = size / CARDS_PER_LINE - 2;// 399
            if (size % CARDS_PER_LINE != 0) {// 400
                ++scrollTmp;// 401
            }

            this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT + (float)scrollTmp * padY;// 403
        } else {
            this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;// 405
        }

    }// 407

    private void resetScrolling() {
        if (this.currentDiffY < this.scrollLowerBound) {// 413
            this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollLowerBound);// 414
        } else if (this.currentDiffY > this.scrollUpperBound) {// 415
            this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollUpperBound);// 416
        }

    }// 418

    public void scrolledUsingBar(float newPercent) {
        this.currentDiffY = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);// 546
        this.updateBarPosition();// 547
    }// 548

    private void updateBarPosition() {
        float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.currentDiffY);// 551
        this.scrollBar.parentScrolledToPercent(percent);// 552
    }// 553

    private void refreshDropdownMenu(DropdownMenu menu) {
        try {
            Object o = ReflectionHacks.getPrivate(menu, DropdownMenu.class, "selectionBox");
            ReflectionHacks.privateMethod(DropdownMenu.class, "changeSelectionToRow", Class.forName(DropdownMenu.class.getName()+"$DropdownRow")).invoke(menu, o);
            DropdownColoring.RowToColor.function.set(augmentDropdown, index -> ChimeraMonstersMod.disabledModifiers.contains(augmentMap.get(getDropdownText(augmentDropdown, index))) ? DISABLE_COLOR : null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getDropdownText(DropdownMenu menu, int index) {
        if (dropdownRowClass == null) {
            try {
                dropdownRowClass = Class.forName(DropdownMenu.class.getName()+"$DropdownRow");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        Object dropdownRow = augmentDropdown.rows.get(index);
        return ReflectionHacks.getPrivate(dropdownRow, dropdownRowClass, "text");
    }

    @SpirePatch2(clz = BaseMod.class, method = "registerModBadge")
    public static class GrabBadge {
        @SpireInsertPatch(locator = Locator.class, localvars = {"badge"})
        public static void plz(ModBadge badge) {
            if (ChimeraMonstersMod.EXTRA_TEXT != null) {
                if (ChimeraMonstersMod.EXTRA_TEXT[0].equals(ReflectionHacks.getPrivate(badge, ModBadge.class, "modName"))) {
                    myBadge = badge;
                }
            }
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.MethodCallMatcher(ArrayList.class, "add");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }
}
