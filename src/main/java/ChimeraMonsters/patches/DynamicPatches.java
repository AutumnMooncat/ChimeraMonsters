package ChimeraMonsters.patches;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.util.analysis.CtClassAnalyzer;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.*;
import org.clapper.util.classutil.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class DynamicPatches {
    @SpirePatch(clz = CardCrawlGame.class, method = SpirePatch.CONSTRUCTOR)
    public static class AbstractMonsterDynamicPatch {
        @SpireRawPatch
        public static void patch(CtBehavior ctBehavior) throws NotFoundException {
            ChimeraMonstersMod.logger.info("");
            ChimeraMonstersMod.logger.info("Starting AbstractMonster dynamic patch");
            long start = System.currentTimeMillis();
            ClassFinder finder = new ClassFinder();
            finder.add(new File(Loader.STS_JAR));

            for (ModInfo modInfo : Loader.MODINFOS) {
                if (modInfo.jarURL != null) {
                    try {
                        finder.add(new File(modInfo.jarURL.toURI()));
                    } catch (URISyntaxException ignored) {}
                }
            }

            ClassFilter filter = new AndClassFilter(
                    new NotClassFilter(new InterfaceOnlyClassFilter()),
                    new ClassModifiersClassFilter(Modifier.PUBLIC),
                    new SubclassClassFilter(AbstractMonster.class)
            );

            ArrayList<ClassInfo> foundClasses = new ArrayList<>();
            finder.findClasses(foundClasses, filter);

            for (ClassInfo classInfo : foundClasses) {
                CtClass ctClass = ctBehavior.getDeclaringClass().getClassPool().get(classInfo.getClassName());
                Matcher damageCall = new Matcher.MethodCallMatcher(AbstractMonster.class, "damage");
                Matcher dieCall = new Matcher.MethodCallMatcher(AbstractMonster.class, "die");
                try {
                    CtMethod[] methods = ctClass.getDeclaredMethods();
                    for (CtMethod m : methods) {
                        if (m.getName().equals("changeState")) {
                            m.insertBefore(MoveManipulationPatches.class.getName() + ".beginStateChange($0);");
                            m.insertAfter(MoveManipulationPatches.class.getName() + ".endStateChange($0);");
                        }
                        if (m.getName().equals("damage")) {
                            m.insertBefore(MoveManipulationPatches.class.getName() + ".beginStateChange($0);");
                            m.insertAfter(MoveManipulationPatches.class.getName() + ".endStateChange($0);");
                            if (!CtClassAnalyzer.performTest(m, damageCall)) {
                                ChimeraMonstersMod.logger.info("Manually account for class {} that overrides damage and does not call super", ctClass.getName());
                                m.insertAfter(MonsterCantDiePatches.DontBonkDamage.class.getName() + ".plz($0);");
                            }
                        }
                        if (m.getName().equals("die")) {
                            m.insertBefore("if(" + MonsterCantDiePatches.DontBonkDie.class.getName() + ".plz($0).isPresent()) {return;}");
                        }
                    }
                } catch (CannotCompileException ignored) {}
            }
            long end = System.currentTimeMillis();
            ChimeraMonstersMod.logger.info("AbstractMonster dynamic patch finished in {}ms", end-start);
        }

        /*@SpireRawPatch
        public static void test(CtBehavior ctBehavior) {
            ChimeraMonstersMod.logger.info("Performing deep class scan");
            long start = System.currentTimeMillis();
            int classCount = 0, methodCount = 0, errorCount = 0;

            ClassFinder finder = new ClassFinder();
            finder.add(new File(Loader.STS_JAR));

            for (ModInfo modInfo : Loader.MODINFOS) {
                if (modInfo.jarURL != null) {
                    try {
                        finder.add(new File(modInfo.jarURL.toURI()));
                    } catch (URISyntaxException ignored) {}
                }
            }

            ArrayList<ClassInfo> foundClasses = new ArrayList<>();
            finder.findClasses(foundClasses, null);

            for (ClassInfo classInfo : foundClasses) {
                try {
                    CtClass ctClass = ctBehavior.getDeclaringClass().getClassPool().get(classInfo.getClassName());
                    classCount++;
                    CtMethod[] methods = ctClass.getDeclaredMethods();
                    for (CtMethod m : methods) {
                        methodCount++;
                    }
                } catch (NotFoundException e) {
                    errorCount++;
                }
            }

            long end = System.currentTimeMillis();
            ChimeraMonstersMod.logger.info("Finished deep class scan, found {} classes and {} methods, threw {} exceptions, took {}ms", classCount, methodCount, errorCount, end-start);
        }*/
    }
}
