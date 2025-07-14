package ChimeraMonsters.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class InterruptUseCardFieldPatches {
    public static boolean doIntercept(AbstractCard card) {
        return false;
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "useCard")
    public static class InterceptPls {
        @SpireInstrumentPatch
        public static ExprEditor patch() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getMethodName().equals("use")) {
                        m.replace(String.format("if (!%s.doIntercept($0)) {$proceed($$);}", InterruptUseCardFieldPatches.class.getName()));
                    }
                }
            };
        }
    }
}
