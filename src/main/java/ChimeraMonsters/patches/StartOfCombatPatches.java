package ChimeraMonsters.patches;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.util.FightModificationManager;
import basemod.devcommands.fight.Fight;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.vfx.combat.BattleStartEffect;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

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

    @SpirePatch2(clz = BattleStartEffect.class, method = "render")
    public static class InsertFightName {
        @SpireInstrumentPatch
        public static ExprEditor patch() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    ChimeraMonstersMod.logger.info("patch triggered");
                    if (m.getClassName().equals(FontHelper.class.getName()) && m.getMethodName().equals("renderFontCentered") && m.getLineNumber() ==  223) {
                        m.replace("$3 = "+StartOfCombatPatches.class.getName()+".getBattleStartText();" +
                                "$_ = $proceed($$);");
                    }
                }
            };
        }
    }

    @SpirePatch2(clz = BattleStartEffect.class, method = "render")
    public static class MakeFightNameDisappearSlowly {
        @SpirePostfixPatch()
        public static void makeSlow(BattleStartEffect __instance) {
            if(!FightModificationManager.fightName.equals("")){
                __instance.duration=8.0f;
                __instance.startingDuration=8.0f;
                //TODO: FIX
            }
        }
    }
}
