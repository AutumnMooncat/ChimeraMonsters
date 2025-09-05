package ChimeraMonsters.util.analysis;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.*;

import java.lang.reflect.Field;
import java.util.HashMap;

public class ActionAnalyzer {
    private static final HashMap<Class<?>, Boolean> doesDamage = new HashMap<>();

    public static DamageInfo findDamage(AbstractGameAction action) {
        Class<?> clazz = action.getClass();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType() == DamageInfo.class) {
                    if (field.getDeclaringClass() == action.getClass()) {
                        return ReflectionHacks.getPrivate(action, action.getClass(), field.getName());
                    } else {
                        return ReflectionHacks.getPrivateInherited(action, action.getClass(), field.getName());
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public static boolean doesDamage(AbstractGameAction action) {
        if (doesDamage.containsKey(action.getClass())) {
            return doesDamage.get(action.getClass());
        }
        DamageInfo info = findDamage(action);
        if (info != null) {
            doesDamage.put(action.getClass(), true);
            return true;
        }
        final boolean[] found = {false};
        ClassPool pool = Loader.getClassPool();
        try {
            CtClass ctPower = pool.get(action.getClass().getName());
            ctPower.defrost();
            for (CtMethod method : ctPower.getDeclaredMethods()) {
                method.instrument(new ExprEditor() {
                    @Override
                    public void edit(NewExpr e) {
                        if (e.getClassName().equals(DamageInfo.class.getName())) {
                            found[0] = true;
                        }
                    }
                });
            }
        } catch (Exception ignored) {}
        doesDamage.put(action.getClass(), found[0]);
        return found[0];
    }
}
