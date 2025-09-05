package ChimeraMonsters.util.matchers;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.expr.Expr;
import javassist.expr.NewExpr;

import java.util.Arrays;

public class InterfaceMatcher extends Matcher.NewExprMatcher {
    String interfaceToCheck;

    public InterfaceMatcher(Class<?> clazz) {
        super(clazz);
        this.interfaceToCheck = clazz.getName();
    }

    public InterfaceMatcher(String className) {
        super(className);
        this.interfaceToCheck = className;
    }

    @Override
    public boolean match(Expr toMatch) {
        NewExpr expr = (NewExpr) toMatch;
        try {
            CtClass ctExprClass = Loader.getClassPool().get(expr.getClassName());
            CtClass ctClass = Loader.getClassPool().getCtClass(interfaceToCheck);
            if (Arrays.asList(ctExprClass.getInterfaces()).contains(ctClass)) {
                return true;
            }
        } catch (NotFoundException ignored) {}
        return false;
    }
}
