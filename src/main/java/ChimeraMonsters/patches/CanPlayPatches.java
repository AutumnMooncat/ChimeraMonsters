package ChimeraMonsters.patches;

import ChimeraMonsters.powers.interfaces.MonsterPreventPlayingCardsPower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class CanPlayPatches {
    @SpirePatch(clz = AbstractCard.class, method = "hasEnoughEnergy")
    public static class ModifierCanPlayCard {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> check(AbstractCard __instance) {
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                for (AbstractPower power : monster.powers) {
                    if (power instanceof MonsterPreventPlayingCardsPower) {
                        if (((MonsterPreventPlayingCardsPower) power).preventPlaying(__instance)) {
                            return SpireReturn.Return(false);
                        }
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "canUse")
    public static class ModifierCanUseCard {
        @SpirePostfixPatch
        public static boolean check(AbstractCard __instance, AbstractMonster m, boolean __result) {
            for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
                for (AbstractPower power : monster.powers) {
                    if (power instanceof MonsterPreventPlayingCardsPower) {
                        if (((MonsterPreventPlayingCardsPower) power).preventUsing(__instance, m)) {
                            return false;
                        }
                    }
                }
            }
            return __result;
        }
    }
}
