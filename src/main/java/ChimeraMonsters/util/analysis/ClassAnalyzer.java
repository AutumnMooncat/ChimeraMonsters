package ChimeraMonsters.util.analysis;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

public class ClassAnalyzer {
    public static boolean doesntOverride(AbstractMonster monster, String method, Class<?>... paramtypez) {
        return doesntOverride(monster, AbstractMonster.class, method, paramtypez);
    }

    public static boolean doesntOverride(Object o, Class<?> clazz, String method, Class<?>... paramtypez) {
        try {
            return o.getClass().getMethod(method, paramtypez).getDeclaringClass().equals(clazz);
        } catch (NoSuchMethodException ignored) {}
        return false;
    }

    public static boolean overridesMethod(Object o, Class<?> clazz, String method, Class<?>... paramtypez) {
        try {
            return !o.getClass().getMethod(method, paramtypez).getDeclaringClass().equals(clazz);
        } catch (NoSuchMethodException ignored) {}
        return false;
    }

    public static boolean hasBlockAction(AbstractMonster monster) {
        return usesAction(monster, GainBlockAction.class);
    }

    public static boolean usesAction(AbstractMonster monster, Class<? extends AbstractGameAction> clazz) {
        return usesClass(monster, clazz);
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
