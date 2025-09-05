package ChimeraMonsters.util.matchers;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;
import javassist.expr.Expr;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.NewExpr;

public class PowerTypeMatcher extends Matcher.NewExprMatcher {
    AbstractPower.PowerType type;

    public PowerTypeMatcher(AbstractPower.PowerType type) {
        super(AbstractPower.class);
        this.type = type;
    }

    @Override
    public boolean match(Expr toMatch) {
        NewExpr expr = (NewExpr) toMatch;
        try {
            CtClass ctExprClass = Loader.getClassPool().get(expr.getClassName());
            CtClass ctPowerClass = Loader.getClassPool().getCtClass(AbstractPower.class.getName());
            ctExprClass.defrost();
            if (ctExprClass != ctPowerClass && ctExprClass.subclassOf(ctPowerClass)) {
                if (type == AbstractPower.PowerType.BUFF) {
                    boolean[] ret = {true};
                    for (CtConstructor ctcon : ctExprClass.getConstructors()) {
                        ctcon.instrument(new ExprEditor() {
                            @Override
                            public void edit(FieldAccess f) {
                                if (f.getClassName().equals(AbstractPower.PowerType.class.getName()) && !f.getFieldName().equals(type.name())) {
                                    ret[0] = false;
                                }
                            }
                        });
                    }
                    return ret[0];
                } else {
                    boolean[] ret = {false};
                    for (CtConstructor ctcon : ctExprClass.getConstructors()) {
                        ctcon.instrument(new ExprEditor() {
                            @Override
                            public void edit(FieldAccess f) {
                                if (f.getClassName().equals(AbstractPower.PowerType.class.getName()) && f.getFieldName().equals(type.name())) {
                                    ret[0] = true;
                                }
                            }
                        });
                    }
                    return ret[0];
                }
            }
        } catch (NotFoundException | CannotCompileException ignored) {}
        return false;
    }
}
