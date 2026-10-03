package ChimeraMonsters.patches.events;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.util.EventHelper;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.FaceTrader;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class FaceTraderPatches {
    public static final String ID = ChimeraMonstersMod.makeID("BrandNewYou");
    public static final EventStrings STRINGS = CardCrawlGame.languagePack.getEventString(ID);

    @SpirePatch2(clz = FaceTrader.class, method = "buttonEffect")
    public static class ButtonPatch {
        public static boolean shouldAdd;
        public static boolean clickedOurButton;
        public static boolean fightTime;
        public static int ourButtonIndex;
        public static int replacedButtonIndex;

        @SpirePrefixPatch
        public static SpireReturn<?> capture(FaceTrader __instance, @ByRef int[] buttonPressed, Object ___screen) {
            if (EventHelper.screenIs(___screen, "INTRO")) {
                shouldAdd = true;
            } else if (EventHelper.screenIs(___screen, "MAIN")) {
                if (buttonPressed[0] == ourButtonIndex) {
                    clickedOurButton = true;
                    buttonPressed[0] = replacedButtonIndex;
                    __instance.imageEventText.updateBodyText(STRINGS.DESCRIPTIONS[0]);
                    __instance.imageEventText.clearAllDialogs();
                    __instance.imageEventText.setDialogOption(STRINGS.OPTIONS[1]);
                    return SpireReturn.Return();
                } else if (buttonPressed[0] == replacedButtonIndex) {
                    buttonPressed[0] = ourButtonIndex;
                }
                if (clickedOurButton) {
                    buttonPressed[0] = replacedButtonIndex;
                    clickedOurButton = false;
                    fightTime = true;
                }
            }
            return SpireReturn.Continue();
        }

        @SpirePostfixPatch
        public static void process(FaceTrader __instance, int buttonPressed, Object ___screen) {
            if (shouldAdd) {
                shouldAdd = false;
                replacedButtonIndex = __instance.imageEventText.optionList.size();
                ourButtonIndex = replacedButtonIndex - 1;
                String originalText = __instance.imageEventText.optionList.get(ourButtonIndex).msg;
                __instance.imageEventText.updateDialogOption(ourButtonIndex, STRINGS.OPTIONS[0]);
                __instance.imageEventText.setDialogOption(originalText);
            } else if (fightTime) {
                fightTime = false;
                AbstractRelic face = ReflectionHacks.privateMethod(FaceTrader.class, "getRandomFace").invoke(__instance);
                int gold = ReflectionHacks.getPrivateStatic(FaceTrader.class, "goldReward");
                AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(ID);
                AbstractDungeon.getCurrRoom().rewards.clear();
                AbstractDungeon.getCurrRoom().addRelicToRewards(face);
                AbstractDungeon.getCurrRoom().addGoldToRewards(gold);
                AbstractDungeon.lastCombatMetricKey = ID;
                __instance.enterCombatFromImage();
                __instance.imageEventText.clearRemainingOptions();
                EventHelper.makeEndAtCombatRewards(__instance);
            }
        }
    }
}
