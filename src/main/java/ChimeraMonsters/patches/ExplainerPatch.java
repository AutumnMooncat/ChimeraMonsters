package ChimeraMonsters.patches;

import ChimeraMonsters.powers.ModifierExplainerPower;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;

import java.util.ArrayList;

public class ExplainerPatch {
    @SpirePatch2(clz = AbstractMonster.class, method = "renderTip")
    public static class CheckShow {
        @SpireInsertPatch(locator = Locator.class)
        public static void plz(AbstractMonster __instance, ArrayList<PowerTip> ___tips) {
            for (AbstractPower p : __instance.powers) {
                if (p instanceof ModifierExplainerPower) {
                    ___tips.add(new PowerTip(p.name, p.description, (Texture) null));
                }
            }
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.MethodCallMatcher(ArrayList.class, "isEmpty");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }
}
