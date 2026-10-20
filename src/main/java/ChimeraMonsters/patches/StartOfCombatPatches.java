package ChimeraMonsters.patches;

import ChimeraMonsters.ChimeraMonstersController;
import ChimeraMonsters.modifiers.groups.AbstractMonsterGroupModifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.util.FightModificationManager;
import ChimeraMonsters.vfx.CustomBattleStartEffect;
import basemod.patches.com.megacrit.cardcrawl.helpers.TopPanel.TopPanelHelper;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.vfx.combat.BattleStartEffect;
import javassist.CtBehavior;

public class StartOfCombatPatches {
    @SpirePatch2(clz = MonsterGroup.class, method = "init")
    public static class RollOnCombatStart {
        @SpirePrefixPatch()
        public static void plz(MonsterGroup __instance) {
            TopPanelHelper.topPanelGroup.removePanelItem(ChimeraMonstersController.explainer);
            ChimeraMonstersController.explainer.reset();
            ChimeraMonstersController.explainerPresent = false;
            FightModificationManager.rollFightModifiers(__instance);
        }
    }

    public static String getBattleStartText() {
        String normal = CardCrawlGame.languagePack.getUIString("BattleStartEffect").TEXT[0];
        String customName = "";
        if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().monsters != null) {
            customName = MonsterGroupFields.fightName.get(AbstractDungeon.getCurrRoom().monsters);
        }
        return customName.isEmpty() ? normal : customName;
    }

    @SpirePatch2(clz = BattleStartEffect.class, method = SpirePatch.CONSTRUCTOR)
    public static class MakeFightNameDisappearSlowly {
        @SpirePostfixPatch()
        public static void makeDisappear(BattleStartEffect __instance) {
            __instance.isDone=true;
            AbstractDungeon.topLevelEffects.add(new CustomBattleStartEffect(false));
        }
    }

    @SpirePatch2(clz = MonsterGroup.class, method = "usePreBattleAction")
    public static class DoPreBattleStuff {
        @SpirePostfixPatch
        public static void alsoTheseOnes(MonsterGroup __instance) {
            for (AbstractMonsterGroupModifier mod : MonsterGroupFields.receivedModifiers.get(__instance)) {
                mod.preBattleActions(__instance);
            }
        }

        @SpireInsertPatch(locator = Locator.class, localvars = {"m"})
        public static void plz(MonsterGroup __instance, AbstractMonster m) {
            for (AbstractMonsterModifier mod : MonsterFields.receivedModifiers.get(m)) {
                mod.preBattleActions(m, __instance);
            }
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(AbstractMonster.class, "useUniversalPreBattleAction"));
            }
        }
    }
}
