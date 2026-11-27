package ChimeraMonsters.util.analysis;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.util.HashMap;
import java.util.function.Predicate;

public class ClassAnalyzer {
    public static boolean doesntOverride(Object o, Class<?> superClazz, String method, Class<?>... paramtypez) {
        return doesntOverride(o.getClass(), superClazz, method, paramtypez);
    }

    public static boolean doesntOverride(Class<?> clazz, Class<?> superClazz, String method, Class<?>... paramtypez) {
        try {
            return clazz.getMethod(method, paramtypez).getDeclaringClass().equals(superClazz);
        } catch (NoSuchMethodException ignored) {}
        return false;
    }

    public static boolean overridesMethod(Object o, Class<?> superClazz, String method, Class<?>... paramtypez) {
        return overridesMethod(o.getClass(), superClazz, method, paramtypez);
    }

    public static boolean overridesMethod(Class<?> clazz, Class<?> superClazz, String method, Class<?>... paramtypez) {
        try {
            return !clazz.getMethod(method, paramtypez).getDeclaringClass().equals(superClazz);
        } catch (NoSuchMethodException ignored) {}
        return false;
    }

    public static boolean methodHasAllClass(Object o, String method, Class<?>... clazzez) {
        return methodHasAllClass(o.getClass(), method, clazzez);
    }

    public static boolean methodHasAllClass(Class<?> source, String method, Class<?>... clazzez) {
        try {
            ClassPool pool = Loader.getClassPool();
            CtClass ctClass = pool.get(source.getName());
            CtMethod ctMethod = ctClass.getDeclaredMethod(method);
            for (Class<?> clazz : clazzez) {
                if (!CtClassAnalyzer.performTest(ctMethod, new Matcher.NewExprMatcher(clazz.getName()))) {
                    return false;
                }
            }
            return true;
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean methodHasAnyClass(Object o, String method, Class<?>... clazzez) {
        return methodHasAnyClass(o.getClass(), method, clazzez);
    }

    public static boolean methodHasAnyClass(Class<?> source, String method, Class<?>... clazzez) {
        try {
            ClassPool pool = Loader.getClassPool();
            CtClass ctClass = pool.get(source.getName());
            CtMethod ctMethod = ctClass.getDeclaredMethod(method);
            for (Class<?> clazz : clazzez) {
                if (CtClassAnalyzer.performTest(ctMethod, new Matcher.NewExprMatcher(clazz.getName()))) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean methodHasAnyMatchers(Object o, String method, Matcher... matchers) {
        return methodHasAnyMatchers(o.getClass(), method, matchers);
    }

    public static boolean methodHasAnyMatchers(Class<?> source, String method, Matcher... matchers) {
        try {
            ClassPool pool = Loader.getClassPool();
            CtClass ctClass = pool.get(source.getName());
            CtMethod ctMethod = ctClass.getDeclaredMethod(method);
            for (Matcher match : matchers) {
                if (CtClassAnalyzer.performTest(ctMethod, match)) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean methodHasAllMatchers(Object o, String method, Matcher... matchers) {
        return methodHasAllMatchers(o.getClass(), method, matchers);
    }

    public static boolean methodHasAllMatchers(Class<?> source, String method, Matcher... matchers) {
        try {
            ClassPool pool = Loader.getClassPool();
            CtClass ctClass = pool.get(source.getName());
            CtMethod ctMethod = ctClass.getDeclaredMethod(method);
            for (Matcher match : matchers) {
                if (!CtClassAnalyzer.performTest(ctMethod, match)) {
                    return false;
                }
            }
            return true;
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean classHasAllClass(Object o, Class<?>... clazzez) {
        return classHasAllClass(o.getClass(), clazzez);
    }

    public static boolean classHasAllClass(Class<?> source, Class<?>... clazzez) {
        return classHasAllClassWhere(source, ctm -> true, clazzez);
    }

    public static boolean classHasAllClassWhere(Object o, Predicate<CtMethod> where, Class<?>... clazzez) {
        return classHasAllClassWhere(o.getClass(), where, clazzez);
    }

    public static boolean classHasAllClassWhere(Class<?> source, Predicate<CtMethod> where, Class<?>... clazzez) {
        try {
            HashMap<Class<?>, Boolean> checks = new HashMap<>();
            for (Class<?> clazz : clazzez) {
                checks.put(clazz, false);
            }
            ClassPool pool = Loader.getClassPool();
            CtClass ctClass = pool.get(source.getName());
            for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
                if (where.test(ctMethod)) {
                    for (Class<?> clazz : clazzez) {
                        if (CtClassAnalyzer.performTest(ctMethod, new Matcher.NewExprMatcher(clazz.getName()))) {
                            checks.put(clazz, true);
                        }
                    }
                }
            }
            return checks.values().stream().allMatch(Boolean::booleanValue);
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean classHasAnyClass(Object o, Class<?>... clazzez) {
        return classHasAnyClass(o.getClass(), clazzez);
    }

    public static boolean classHasAnyClass(Class<?> source, Class<?>... clazzez) {
        return classHasAnyClassWhere(source, ctm -> true, clazzez);
    }

    public static boolean classHasAnyClassWhere(Object o, Predicate<CtMethod> where, Class<?>... clazzez) {
        return classHasAnyClassWhere(o.getClass(), where, clazzez);
    }

    public static boolean classHasAnyClassWhere(Class<?> source, Predicate<CtMethod> where, Class<?>... clazzez) {
        try {
            ClassPool pool = Loader.getClassPool();
            CtClass ctClass = pool.get(source.getName());
            for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
                if (where.test(ctMethod)) {
                    for (Class<?> clazz : clazzez) {
                        if (CtClassAnalyzer.performTest(ctMethod, new Matcher.NewExprMatcher(clazz.getName()))) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean classHasAnyMatchers(Object o, Matcher... matchers) {
        return classHasAnyMatchers(o.getClass(), matchers);
    }

    public static boolean classHasAnyMatchers(Class<?> source, Matcher... matchers) {
        return classHasAnyMatchersWhere(source, ctm -> true, matchers);
    }

    public static boolean classHasAnyMatchersWhere(Object o, Predicate<CtMethod> where, Matcher... matchers) {
        return classHasAnyMatchersWhere(o.getClass(), where, matchers);
    }

    public static boolean classHasAnyMatchersWhere(Class<?> source, Predicate<CtMethod> where, Matcher... matchers) {
        try {
            ClassPool pool = Loader.getClassPool();
            CtClass ctClass = pool.get(source.getName());
            for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
                if (where.test(ctMethod)) {
                    for (Matcher match : matchers) {
                        if (CtClassAnalyzer.performTest(ctMethod, match)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean classHasAllMatchers(Object o, Matcher... matchers) {
        return classHasAllMatchers(o.getClass(), matchers);
    }

    public static boolean classHasAllMatchers(Class<?> source, Matcher... matchers) {
        return classHasAnyMatchersWhere(source, ctm -> true, matchers);
    }

    public static boolean classHasAllMatchersWhere(Object o, Predicate<CtMethod> where, Matcher... matchers) {
        return classHasAllMatchersWhere(o.getClass(), where, matchers);
    }

    public static boolean classHasAllMatchersWhere(Class<?> source, Predicate<CtMethod> where, Matcher... matchers) {
        try {
            HashMap<Matcher, Boolean> checks = new HashMap<>();
            for (Matcher match : matchers) {
                checks.put(match, false);
            }
            ClassPool pool = Loader.getClassPool();
            CtClass ctClass = pool.get(source.getName());
            for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
                if (where.test(ctMethod)) {
                    for (Matcher match : matchers) {
                        if (CtClassAnalyzer.performTest(ctMethod, match)) {
                            checks.put(match, true);
                        }
                    }
                }
            }
            return checks.values().stream().allMatch(Boolean::booleanValue);
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean classReferencesField(Object o, Class<?> containingClazz, String fieldName) {
        return classReferencesField(o.getClass(), containingClazz, fieldName);
    }

    public static boolean classReferencesField(Class<?> source, Class<?> containingClazz, String fieldName) {
        return classReferencesFieldWhere(source, containingClazz, fieldName, ctm -> true);
    }

    public static boolean classReferencesFieldWhere(Object o, Class<?> containingClazz, String fieldName, Predicate<CtMethod> where) {
        return classReferencesFieldWhere(o.getClass(), containingClazz, fieldName, where);
    }

    public static boolean classReferencesFieldWhere(Class<?> source, Class<?> containingClazz, String fieldName, Predicate<CtMethod> where) {
        try {
            ClassPool pool = Loader.getClassPool();
            CtClass ctClass = pool.get(source.getName());
            for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
                if (where.test(ctMethod)) {
                    if (CtClassAnalyzer.performTest(ctMethod, new Matcher.FieldAccessMatcher(containingClazz, fieldName))) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }
}
