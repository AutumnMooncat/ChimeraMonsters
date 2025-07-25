package ChimeraMonsters.util.matchers;

import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import javassist.expr.Expr;
import javassist.expr.FieldAccess;

public class SuperFieldAccessMatcher extends Matcher.FieldAccessMatcher {
    String classToCheck;
    String fieldName;

    public SuperFieldAccessMatcher(Class<?> clazz, String fieldName) {
        super(clazz, fieldName);
        this.classToCheck = clazz.getName();
        this.fieldName = fieldName;
    }

    public SuperFieldAccessMatcher(String className, String fieldName) {
        super(className, fieldName);
        this.classToCheck = className;
        this.fieldName = fieldName;
    }

    @Override
    public boolean match(Expr toMatch) {
        FieldAccess expr = (FieldAccess) toMatch;
        try {
            Class<?> superClazz = Class.forName(classToCheck, false, getClass().getClassLoader());
            Class<?> clazz = Class.forName(expr.getEnclosingClass().getName(), false, getClass().getClassLoader());
            return expr.getFieldName().equals(this.fieldName) && superClazz.isAssignableFrom(clazz);
        } catch (Exception ignored) {}
        return false;
    }
}
