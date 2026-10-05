package ChimeraMonsters.util.matchers;

import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import javassist.expr.Expr;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

public class SuperMethodCallMatcher extends Matcher.MethodCallMatcher {
    String classToCheck;
    String methodName;

    public SuperMethodCallMatcher(Class<?> clazz, String methodName) {
        super(clazz, methodName);
        this.classToCheck = clazz.getName();
        this.methodName = methodName;
    }

    public SuperMethodCallMatcher(String className, String methodName) {
        super(className, methodName);
        this.classToCheck = className;
        this.methodName = methodName;
    }

    @Override
    public boolean match(Expr toMatch) {
        MethodCall expr = (MethodCall) toMatch;
        try {
            Class<?> superClazz = Class.forName(classToCheck, false, getClass().getClassLoader());
            Class<?> clazz = Class.forName(expr.getEnclosingClass().getName(), false, getClass().getClassLoader());
            return expr.getMethodName().equals(this.methodName) && superClazz.isAssignableFrom(clazz);
        } catch (Exception ignored) {}
        return false;
    }
}
