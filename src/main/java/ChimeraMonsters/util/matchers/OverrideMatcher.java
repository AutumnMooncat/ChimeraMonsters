package ChimeraMonsters.util.matchers;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.Expr;
import javassist.expr.NewExpr;

public class OverrideMatcher extends Matcher.NewExprMatcher {
    String classToCheck;
    String methodName;
    Class<?>[] paramtypez;

    public OverrideMatcher(Class<?> clazz, String methodName, Class<?>... paramtypez) {
        super(clazz);
        this.classToCheck = clazz.getName();
        this.methodName = methodName;
        this.paramtypez = paramtypez;
    }

    public OverrideMatcher(String className, String methodName, Class<?>... paramtypez) {
        super(className);
        this.classToCheck = className;
        this.methodName = methodName;
        this.paramtypez = paramtypez;
    }

    @Override
    public boolean match(Expr toMatch) {
        NewExpr expr = (NewExpr) toMatch;
        try {
            CtClass ctExprClass = Loader.getClassPool().get(expr.getClassName());
            CtClass ctClass = Loader.getClassPool().getCtClass(classToCheck);
            if (ctExprClass != ctClass && ctExprClass.subclassOf(ctClass)) {
                for (CtMethod methodToCheck : ctExprClass.getDeclaredMethods()) {
                    if (methodToCheck.getMethodInfo2().getName().equals(methodName)) {
                        return true;
                    }
                }
            }
        } catch (NotFoundException ignored) {}
        return false;
    }
}
