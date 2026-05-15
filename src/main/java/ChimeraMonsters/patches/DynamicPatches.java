package ChimeraMonsters.patches;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
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
                try {
                    CtMethod[] methods = ctClass.getDeclaredMethods();
                    for (CtMethod m : methods) {
                        if (m.getName().equals("changeState")) {
                            m.insertBefore(MoveManipulationPatches.class.getName() + ".beginStateChange($0);");
                            m.insertAfter(MoveManipulationPatches.class.getName() + ".endStateChange($0);");
                        }
                    }
                } catch (CannotCompileException ignored) {}
            }
        }
    }
}
