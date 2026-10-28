package ChimeraMonsters.patches;

import ChimeraMonsters.ui.HoveringCardManager;
import ChimeraMonsters.util.MonsterOrbHelper;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.stances.AbstractStance;

import java.util.ArrayList;

public class CreatureUpdatePatches {
    @SpirePatch2(clz = AbstractMonster.class, method = "update")
    public static class UpdateTime {
        @SpirePrefixPatch
        public static void preUpdate(AbstractMonster __instance) {
            AbstractStance stance = MonsterFields.stance.get(__instance);
            if (stance != null) {
                stance.update();
            }

            ArrayList<AbstractOrb> orbs = MonsterOrbHelper.getOrbs(__instance);
            if (orbs != null) {
                int evokes = MonsterOrbHelper.intendedEvokes(__instance);
                for (AbstractOrb orb : orbs) {
                    orb.update();
                    orb.updateAnimation();
                    if (orbs.indexOf(orb) < evokes) {
                        orb.showEvokeValue();
                    } else {
                        orb.hideEvokeValues();
                    }
                }
            }
        }

        @SpirePostfixPatch
        public static void postUpdate(AbstractMonster __instance) {
            HoveringCardManager manager = MonsterFields.cardManager.get(__instance);
            if (manager != null) {
                manager.update();
            }
        }
    }
}
