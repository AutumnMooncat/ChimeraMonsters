package ChimeraMonsters.util.analysis;

import ChimeraMonsters.powers.MonsterDexterityPower;
import ChimeraMonsters.powers.MonsterFrailPower;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import javassist.*;
import javassist.expr.*;

import java.util.Arrays;
import java.util.HashMap;

public class PowerAnalyzer {
    private static final HashMap<Class<?>, Boolean> processed = new HashMap<>();
    private static final Class<?>[] bannedClasses = {MakeTempCardAtBottomOfDeckAction.class, MakeTempCardInDiscardAction.class, MakeTempCardInDiscardAndDeckAction.class, MakeTempCardInDrawPileAction.class, MakeTempCardInHandAction.class};

    public static boolean safeToApply(AbstractPower power) {
        if (processed.containsKey(power.getClass())) {
            return processed.get(power.getClass());
        }
        final boolean[] safe = {true};
        ClassPool pool = Loader.getClassPool();
        try {
            CtClass ctPower = pool.get(power.getClass().getName());
            CtClass ctPlayer = pool.get(AbstractPlayer.class.getName());
            ctPower.defrost();
            for (CtMethod method : ctPower.getDeclaredMethods()) {
                final int[] playerAccessors = {0};
                method.instrument(new ExprEditor() {
                    @Override
                    public void edit(NewExpr e) {
                        if (Arrays.stream(bannedClasses).anyMatch(clz -> clz.getName().equals(e.getClassName()))) {
                            safe[0] = false;
                        }
                    }

                    @Override
                    public void edit(FieldAccess f) {
                        String className = f.getClassName();
                        String fieldName = f.getFieldName();
                        if (className.equals(AbstractDungeon.class.getName()) && fieldName.equals("player")) {
                            playerAccessors[0]++;
                        }
                        if (className.equals(AbstractPlayer.class.getName())) {
                            playerAccessors[0]--;
                        }
                    }

                    @Override
                    public void edit(MethodCall m) {
                        String className = m.getClassName();
                        if (className.equals(AbstractPlayer.class.getName())) {
                            playerAccessors[0]--;
                        }
                    }

                    @Override
                    public void edit(Cast c) {
                        try {
                            if (c.getType().equals(ctPlayer)) {
                                safe[0] = false;
                            }
                        } catch (NotFoundException ignored) {}
                    }
                });
                if (playerAccessors[0] > 0) {
                    safe[0] = false;
                }
            }
        } catch (Exception ignored) {
            safe[0] = false;
        }
        processed.put(power.getClass(), safe[0]);
        return safe[0];
    }

    public static AbstractPower tryGetReplacement(AbstractPower power, AbstractCreature target, AbstractCreature source, int amount) {
        if (power instanceof DexterityPower) {
            return new MonsterDexterityPower(target, amount);
        } else if (power instanceof FrailPower) {
            return new MonsterFrailPower(target, amount);
        }
        return null;
    }

    public static AbstractPower tryGetClone(AbstractPower power, AbstractCreature target, AbstractCreature source, int amount) {
        Class<?> clazz = power.getClass();
        try {
            return (AbstractPower) clazz.getConstructor(AbstractCreature.class).newInstance(target);
        } catch (Exception ignored) {}
        try {
            return (AbstractPower) clazz.getConstructor(AbstractCreature.class, Integer.TYPE).newInstance(target, amount);
        } catch (Exception ignored) {}
        try {
            return (AbstractPower) clazz.getConstructor(AbstractCreature.class, AbstractCreature.class, Integer.TYPE).newInstance(target, source, amount);
        } catch (Exception ignored) {}
        try {
            return (AbstractPower) clazz.getConstructor(AbstractCreature.class, Integer.TYPE, Boolean.TYPE).newInstance(target, amount, true);
        } catch (Exception ignored) {}
        return null;
    }
}
