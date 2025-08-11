package ChimeraMonsters.patches;

import ChimeraMonsters.util.FightModificationManager;
import ChimeraMonsters.vfx.CustomBattleStartEffect;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.vfx.combat.BattleStartEffect;

public class StartOfCombatPatches {
    @SpirePatch2(clz = MonsterGroup.class, method = "init")
    public static class RollOnCombatStart {
        @SpirePrefixPatch()
        public static void plz(MonsterGroup __instance) {
            FightModificationManager.rollFightModifiers(__instance);
        }
    }

    public static String getBattleStartText(){
        return FightModificationManager.fightName.equals("") ? CardCrawlGame.languagePack.getUIString("BattleStartEffect").TEXT[0] : FightModificationManager.fightName;
    }

    @SpirePatch2(clz = BattleStartEffect.class, method = SpirePatch.CONSTRUCTOR)
    public static class MakeFightNameDisappearSlowly {
        @SpirePostfixPatch()
        public static void makeDisappear(BattleStartEffect __instance) {
            __instance.isDone=true;
            AbstractDungeon.topLevelEffects.add(new CustomBattleStartEffect(false));
        }
    }
}
