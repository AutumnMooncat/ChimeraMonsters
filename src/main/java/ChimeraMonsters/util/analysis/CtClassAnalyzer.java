package ChimeraMonsters.util.analysis;

import ChimeraMonsters.util.matchers.*;
import basemod.Pair;
import basemod.abstracts.AbstractCardModifier;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.ExhaustAllEtherealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.watcher.FreeAttackPower;
import com.megacrit.cardcrawl.powers.watcher.MasterRealityPower;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

import java.util.*;

public class CtClassAnalyzer {
    private static final HashMap<CtClass, HashMap<Matcher[], Boolean>> performedCtClassChecks = new HashMap<>();
    private static final HashMap<Class<?>, HashMap<Matcher[], Boolean>> performedClassChecks = new HashMap<>();
    // If we end up in these classes, we know we have gone too far and can stop method call recursion
    public static Class<?>[] bannedMethodChecks = {AbstractDungeon.class, GameActionManager.class, AbstractPlayer.class, AbstractCreature.class, AbstractMonster.class, SpriteBatch.class, FontHelper.class, AbstractCard.class, CustomCard.class};
    // These classes create false positives as they are accidentally included in basegame cards
    public static Class<?>[] bannedClassChecks = {ExhaustAllEtherealAction.class};
    // We want to check objects which extend these
    public static Class<?>[] importantSuperClasses = {AbstractGameAction.class, AbstractPower.class, AbstractOrb.class, AbstractDamageModifier.class, AbstractCardModifier.class};
    // Built from previous array when needed
    public static final ArrayList<CtClass> importantCtSuperClasses = new ArrayList<>();
    // Don't look at methods from classes that start with these
    public static final String[] bannedPaths = {"java", "basemod", "com.badlogic"};

    public static final Matcher[] DEALS_DAMAGE = {
            new Matcher.MethodCallMatcher(AbstractCreature.class, "damage"),
            new Matcher.MethodCallMatcher(AbstractMonster.class, "damage"),
            new Matcher.MethodCallMatcher(AbstractPlayer.class, "damage")
    };
    public static final Matcher[] GAINS_BLOCK = {
            new Matcher.MethodCallMatcher(AbstractCreature.class, "addBlock"),
            new Matcher.MethodCallMatcher(AbstractPlayer.class, "addBlock"),
    };
    public static final Matcher[] HAS_DAMAGE_MITIGATION = {
            // Barricade is hardcoded
            new Matcher.NewExprMatcher(BarricadePower.class),
            new CompoundMatcher(new OverrideMatcher(AbstractPower.class, "atDamageReceive"), new PowerTypeMatcher(AbstractPower.PowerType.BUFF)),
            new CompoundMatcher(new OverrideMatcher(AbstractPower.class, "atDamageFinalReceive"), new PowerTypeMatcher(AbstractPower.PowerType.BUFF)),
            new CompoundMatcher(new OverrideMatcher(AbstractPower.class, "onAttackedToChangeDamage"), new PowerTypeMatcher(AbstractPower.PowerType.BUFF)),
            new CompoundMatcher(new OverrideMatcher(AbstractPower.class, "modifyBlock"), new PowerTypeMatcher(AbstractPower.PowerType.BUFF)),
            new CompoundMatcher(new OverrideMatcher(AbstractPower.class, "modifyBlockLast"), new PowerTypeMatcher(AbstractPower.PowerType.BUFF)),
            new CompoundMatcher(new OverrideMatcher(AbstractPower.class, "atDamageGive"), new PowerTypeMatcher(AbstractPower.PowerType.DEBUFF)),
            new Matcher.FieldAccessMatcher(TempHPField.class, "tempHp"),
    };
    public static final Matcher[] DOES_ORB_MANIPULATION = {
            new Matcher.NewExprMatcher(FocusPower.class),
            new Matcher.NewExprMatcher(LockOnPower.class),
            new Matcher.MethodCallMatcher(AbstractOrb.class, "onStartOfTurn"),
            new Matcher.MethodCallMatcher(AbstractOrb.class, "onEndOfTurn"),
            new Matcher.MethodCallMatcher(AbstractPlayer.class, "evokeWithoutLosingOrb"),
            new Matcher.MethodCallMatcher(AbstractPlayer.class, "increaseMaxOrbSlots"),
            new Matcher.MethodCallMatcher(AbstractPlayer.class, "decreaseMaxOrbSlots"),
            new Matcher.MethodCallMatcher(AbstractPlayer.class, "evokeOrb"),
            new Matcher.MethodCallMatcher(AbstractPlayer.class, "removeNextOrb")
    };
    public static final Matcher[] ENERGY_MANIPULATION = {
            new Matcher.NewExprMatcher(FreeAttackPower.class),
            new Matcher.MethodCallMatcher(AbstractPlayer.class, "gainEnergy"),
            new Matcher.MethodCallMatcher(AbstractCard.class, "setCostForTurn"),
            new Matcher.MethodCallMatcher(AbstractCard.class, "modifyCostForCombat"),
            new Matcher.FieldAccessMatcher(AbstractCard.class, "costForTurn"),
            new Matcher.FieldAccessMatcher(AbstractCard.class, "isCostModified"),
    };
    public static final Matcher[] HAS_DAMAGE_BOOST = {
            new CompoundMatcher(new OverrideMatcher(AbstractPower.class, "atDamageReceive"), new PowerTypeMatcher(AbstractPower.PowerType.DEBUFF)),
            new CompoundMatcher(new OverrideMatcher(AbstractPower.class, "atDamageFinalReceive"), new PowerTypeMatcher(AbstractPower.PowerType.DEBUFF)),
            new CompoundMatcher(new OverrideMatcher(AbstractPower.class, "atDamageGive"), new PowerTypeMatcher(AbstractPower.PowerType.BUFF)),
            new CompoundMatcher(new OverrideMatcher(AbstractPower.class, "atDamageFinalGive"), new PowerTypeMatcher(AbstractPower.PowerType.BUFF)),
    };
    public static final Matcher[] CARD_MANIPULATION = {
            new Matcher.MethodCallMatcher(AbstractPlayer.class, "draw"),
            new Matcher.MethodCallMatcher(CardGroup.class, "addToHand"),
            new Matcher.MethodCallMatcher(CardGroup.class, "moveToDiscardPile"),
            new Matcher.MethodCallMatcher(CardGroup.class, "moveToExhaustPile"),
            new Matcher.MethodCallMatcher(CardGroup.class, "moveToHand"),
            new Matcher.MethodCallMatcher(CardGroup.class, "moveToDeck"),
            new Matcher.MethodCallMatcher(CardGroup.class, "moveToBottomOfDeck"),
            new Matcher.MethodCallMatcher(CardGroup.class, "addToTop"),
            new Matcher.MethodCallMatcher(CardGroup.class, "addToBottom"),
            new Matcher.MethodCallMatcher(CardGroup.class, "addToRandomSpot")
    };
    public static final Matcher[] CARD_CREATION = {
            new Matcher.NewExprMatcher(MasterRealityPower.class),
            new Matcher.NewExprMatcher(ShowCardAndAddToHandEffect.class),
            new Matcher.NewExprMatcher(ShowCardAndAddToDiscardEffect.class),
            new Matcher.NewExprMatcher(ShowCardAndAddToDrawPileEffect.class)
    };

    public static boolean testObject(Object object, Matcher... matchers) {
        return testClass(object.getClass(), matchers);
    }

    public static boolean testClass(Class<?> clazz, Matcher... matchers) {
        return testClasses(Collections.singletonList(clazz), matchers);
    }

    public static ArrayList<Pair<CtClass, CtMethod>> methodStack = new ArrayList<>();
    public static boolean testClasses(List<Class<?>> classes, Matcher... matchers) {
        ClassPool pool = Loader.getClassPool();
        if (importantCtSuperClasses.isEmpty()) {
            for (Class<?> clazz : importantSuperClasses) {
                try {
                    importantCtSuperClasses.add(pool.get(clazz.getName()));
                } catch (NotFoundException ignored) {}
            }
        }
        for (Class<?> clazz : classes) {
            if (!performedClassChecks.containsKey(clazz)) {
                performedClassChecks.put(clazz, new HashMap<>());
            }
            if (performedClassChecks.get(clazz).containsKey(matchers)) {
                if (performedClassChecks.get(clazz).get(matchers)) {
                    return true;
                }
            } else {
                try {
                    CtClass ctClass = pool.getCtClass(clazz.getName());
                    methodStack.clear();
                    if (ctClassTester(ctClass, matchers)) {
                        performedClassChecks.get(clazz).put(matchers, true);
                        return true;
                    }
                } catch (NotFoundException ignored) {}
                performedClassChecks.get(clazz).put(matchers, false);
            }
        }
        return false;
    }

    public static boolean ctClassTester(CtClass ctClass, Matcher... matchers) {
        if (!performedCtClassChecks.containsKey(ctClass)) {
            performedCtClassChecks.put(ctClass, new HashMap<>());
        }
        if (performedCtClassChecks.get(ctClass).containsKey(matchers)) {
            return performedCtClassChecks.get(ctClass).get(matchers);
        }
        ArrayList<CtMethod> foundMethods = ctMethodFinder(ctClass);
        for (CtMethod currentMethod : foundMethods) {
            if (ctMethodTester(currentMethod, matchers)) {
                performedCtClassChecks.get(ctClass).put(matchers, true);
                return true;
            }
        }
        performedCtClassChecks.get(ctClass).put(matchers, false);
        return false;
    }

    public static ArrayList<CtMethod> ctMethodFinder(CtClass ctClass) {
        ctClass.defrost();
        ArrayList<CtMethod> foundMethods = new ArrayList<>(Arrays.asList(ctClass.getDeclaredMethods()));
        if (importantCtSuperClasses.stream().anyMatch(ctc -> ctClass.subclassOf(ctc) && !ctc.getClassFile2().getName().equals(AbstractOrb.class.getName()))) {
            CtClass currentCtClass = ctClass;
            while (true) {
                try {
                    CtClass superCtClass = currentCtClass.getSuperclass();
                    for (CtMethod m : superCtClass.getDeclaredMethods()) {
                        if (Arrays.stream(bannedMethodChecks).noneMatch(clazz -> m.getDeclaringClass().getName().equals(clazz.getName()))) {
                            if (Arrays.stream(bannedPaths).noneMatch(s -> m.getDeclaringClass().getName().startsWith(s))) {
                                foundMethods.add(m);
                            }
                        }
                    }
                    currentCtClass = superCtClass;
                } catch (Exception ignored) {
                    break;
                }
            }
        }
        return foundMethods;
    }

    public static boolean ctMethodTester(CtMethod check, Matcher... matchers) {
        Pair<CtClass, CtMethod> key = new Pair<>(check.getDeclaringClass(), check);
        if (performTest(check, matchers)) {
            return true;
        }
        if (!methodStack.contains(key)) {
            methodStack.add(key);
            for (CtMethod next : getReferencedMethods(check)) {
                if (ctMethodTester(next, matchers)) {
                    return true;
                }
            }
            for (CtClass next : getReferencedClasses(check)) {
                if (ctClassTester(next, matchers)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean performTest(CtMethod ctMethodToPatch, Matcher... matchers) {
        for (Matcher matcher : matchers) {
            if (matcher instanceof CompoundMatcher) {
                ((CompoundMatcher) matcher).reset();
            }
            BasicMatchChecker editor = new BasicMatchChecker(matcher);
            try {
                ctMethodToPatch.getDeclaringClass().defrost();
                ctMethodToPatch.instrument(editor);// 24
                if (editor.didMatch()) {
                    return true;
                }
            } catch (CannotCompileException | RuntimeException ignored) {}
        }
        return false;
    }

    public static ArrayList<CtMethod> getReferencedMethods(CtMethod method) {
        ArrayList<CtMethod> foundMethods = new ArrayList<>();
        try {
            method.getDeclaringClass().defrost();
            method.instrument(new ExprEditor() {
                @Override
                public void edit(MethodCall m) {
                    try {
                        CtMethod newMethod = m.getMethod();
                        if (Arrays.stream(bannedMethodChecks).noneMatch(clazz -> newMethod.getDeclaringClass().getName().equals(clazz.getName()))) {
                            if (Arrays.stream(bannedPaths).noneMatch(s -> newMethod.getDeclaringClass().getName().startsWith(s))) {
                                foundMethods.add(newMethod);
                            }
                        }
                    } catch (NotFoundException | RuntimeException ignored) {}
                }
            });
        } catch (CannotCompileException ignored) {}
        return foundMethods;
    }

    public static ArrayList<CtClass> getReferencedClasses(CtMethod method) {
        ArrayList<CtClass> foundClasses = new ArrayList<>();
        try {
            method.getDeclaringClass().defrost();
            method.instrument(new ExprEditor() {
                @Override
                public void edit(NewExpr e) {
                    try {
                        CtClass ctExprClass = e.getConstructor().getDeclaringClass();
                        if (Arrays.stream(bannedClassChecks).noneMatch(clazz -> ctExprClass.getClassFile2().getName().equals(clazz.getName()))) {
                            if (importantCtSuperClasses.stream().anyMatch(ctExprClass::subclassOf)) {
                                foundClasses.add(ctExprClass);
                            }
                        }
                    } catch (NotFoundException | RuntimeException ignored) {}
                }
            });
        } catch (CannotCompileException ignored) {}
        return foundClasses;
    }
}
