package ChimeraMonsters.util;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.Loader;
import javassist.*;

public interface PatchHelper {
    static ReflectionHacks.RMethod findOverride(Object o, String methodName, Class<?>... paramtypez) {
        Class<?> clazz = o.getClass();
        ReflectionHacks.RMethod method = null;
        while (method == null && clazz != null) {
            try {
                if (clazz.getMethod(methodName, paramtypez).getDeclaringClass().equals(clazz)) {
                    method = ReflectionHacks.privateMethod(clazz, methodName, paramtypez);
                }
                clazz = clazz.getSuperclass();
            } catch (Exception ignored) {}
        }
        return method;
    }

    static CtClass ctClassOf(Class<?> clazz) {
        try {
            return getClassPool().getCtClass(clazz.getName());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static Class<?> classOf(CtClass ctc) {
        try {
            return Class.forName(ctc.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static ClassPool getClassPool() {
        return Loader.getClassPool();
    }

    static CtClass[] toCtClasses(Class<?>... classes) throws NotFoundException {
        if (classes == null) {
            return null;
        }
        String[] names = new String[classes.length];
        for (int i = 0; i < classes.length; i++) {
            names[i] = classes[i].getName();
        }
        return getClassPool().get(names);
    }

    static CtMethod getDeclaredMethod(CtClass ctc, String method, Class<?>... params) {
        try {
            return getDeclaredMethod(ctc, method, toCtClasses(params));
        } catch (NotFoundException e) {
            return null;
        }
    }

    static CtMethod getDeclaredMethod(CtClass ctc, String method, CtClass... params) {
        try {
            return ctc.getDeclaredMethod(method, params);
        } catch (NotFoundException ignored) {
            return null;
        }
    }

     static CtMethod generateOverride(CtClass ctc, String method, CtClass... params) {
        CtMethod override = getDeclaredMethod(ctc, method, params);
        if (override != null) {
            return override;
        }
        CtClass currentCt = ctc;
        CtMethod superMethod;
        do {
            try {
                currentCt = currentCt.getSuperclass();
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
            superMethod = getDeclaredMethod(currentCt, method, params);
        } while (superMethod == null);
        try {
            override = CtNewMethod.delegator(superMethod, ctc);
            ctc.addMethod(override);
            return override;
        } catch (CannotCompileException e) {
            throw new RuntimeException(e);
        }
    }
}
