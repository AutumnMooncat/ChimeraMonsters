package ChimeraMonsters.util.analysis;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

import java.util.HashMap;

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

    public static boolean classHasAllClass(Object o, Class<?>... clazzez) {
        return classHasAllClass(o.getClass(), clazzez);
    }

    public static boolean classHasAllClass(Class<?> source, Class<?>... clazzez) {
        try {
            HashMap<Class<?>, Boolean> checks = new HashMap<>();
            for (Class<?> clazz : clazzez) {
                checks.put(clazz, false);
            }
            ClassPool pool = Loader.getClassPool();
            CtClass ctClass = pool.get(source.getName());
            for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
                for (Class<?> clazz : clazzez) {
                    if (CtClassAnalyzer.performTest(ctMethod, new Matcher.NewExprMatcher(clazz.getName()))) {
                        checks.put(clazz, true);
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
        try {
            ClassPool pool = Loader.getClassPool();
            CtClass ctClass = pool.get(source.getName());
            for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
                for (Class<?> clazz : clazzez) {
                    if (CtClassAnalyzer.performTest(ctMethod, new Matcher.NewExprMatcher(clazz.getName()))) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean usesClass(AbstractMonster monster, Class<?> clazz) {
        final boolean[] usesAction = {false};
        ClassPool pool = Loader.getClassPool();
        try {
            CtClass ctClass = pool.get(monster.getClass().getName());
            ctClass.defrost();
            CtMethod ctTakeTurn = ctClass.getDeclaredMethod("takeTurn");
            ctTakeTurn.instrument(new ExprEditor() {
                @Override
                public void edit(NewExpr e) {
                    if (e.getClassName().equals(clazz.getName())) {
                        usesAction[0] = true;
                    }
                }

                @Override
                public void edit(MethodCall m) {
                    try {
                        CtMethod check = m.getMethod();
                        check.instrument(new ExprEditor() {
                            @Override
                            public void edit(NewExpr e) {
                                if (e.getClassName().equals(clazz.getName())) {
                                    usesAction[0] = true;
                                }
                            }
                        });
                    } catch (Exception ignored) {}
                }
            });
        } catch (Exception ignored) {}
        return usesAction[0];
    }
}
