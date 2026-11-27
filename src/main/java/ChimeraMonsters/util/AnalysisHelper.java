package ChimeraMonsters.util;

import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.patches.MonsterFields;
import ChimeraMonsters.util.analysis.ClassAnalyzer;
import ChimeraMonsters.util.analysis.CtClassAnalyzer;
import ChimeraMonsters.util.matchers.SuperFieldAccessMatcher;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.actions.unique.SummonGremlinAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.*;
import javassist.CtClass;
import javassist.CtMethod;

import java.util.Collections;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface AnalysisHelper {
    String TAKE_TURN = "takeTurn";
    String PRE_BATTLE = "usePreBattleAction";
    Predicate<CtMethod> isNotTakeTurn = ctm -> !ctm.getName().equals(TAKE_TURN);
    Predicate<CtMethod> isNotPreBattle = ctm -> !ctm.getName().equals(PRE_BATTLE);
    Predicate<AbstractMonster> doesScale = mon -> ClassAnalyzer.classHasAnyClass(mon, RitualPower.class, StrengthPower.class, GenericStrengthUpPower.class);
    Predicate<AbstractMonster> doesSummon = mon -> ClassAnalyzer.classHasAnyClass(mon, SpawnMonsterAction.class, SummonGremlinAction.class, MinionPower.class);
    Predicate<AbstractMonster> isNormal = mon -> mon.type == AbstractMonster.EnemyType.NORMAL;
    Predicate<AbstractMonster> isElite = mon -> mon.type == AbstractMonster.EnemyType.ELITE;
    Predicate<AbstractMonster> isBoss = mon -> mon.type == AbstractMonster.EnemyType.BOSS;
    Predicate<MonsterGroup> singleCombat = (group) -> group.monsters.size() == 1;
    Predicate<MonsterGroup> multiCombat = (group) -> group.monsters.size() > 1;
    Predicate<MonsterGroup> noBosses = (group) -> group.monsters.stream().noneMatch(isBoss);
    Predicate<MonsterGroup> noElites = (group) -> group.monsters.stream().noneMatch(isElite);
    Predicate<MonsterGroup> noElitesOrBosses = (group) -> group.monsters.stream().allMatch(isNormal);
    Predicate<MonsterGroup> noScaling = (group) -> group.monsters.stream().noneMatch(doesScale);
    Predicate<MonsterGroup> noSummoning = (group) -> group.monsters.stream().noneMatch(doesSummon);
    BiPredicate<MonsterGroup, AbstractMonsterModifier> onePerFight = (group, toCheck) -> group.monsters.stream().noneMatch(mon -> MonsterFields.receivedModifiers.get(mon).stream().anyMatch(mod -> mod.identifier().equals(toCheck.identifier())));
    BiPredicate<MonsterGroup, AbstractMonster> lastMonster = (group, mon) -> group.monsters.get(group.monsters.size() - 1) == mon;

    default boolean checkContext(MonsterGroup context, Predicate<AbstractMonster> check, boolean all) {
        if (context == null) return true;
        return all ? context.monsters.stream().allMatch(check) : context.monsters.stream().anyMatch(check);
    }

    default boolean checkContext(MonsterGroup context, Predicate<MonsterGroup> check) {
        return context == null || check.test(context);
    }

    default boolean checkContext(MonsterGroup context, AbstractMonster monster, BiPredicate<MonsterGroup, AbstractMonster> check) {
        return context == null || check.test(context, monster);
    }

    default boolean checkContext(MonsterGroup context, AbstractMonsterModifier mod, BiPredicate<MonsterGroup, AbstractMonsterModifier> check) {
        return context == null || check.test(context, mod);
    }

    default boolean noHalfDead(AbstractMonster monster) {
        return !hasAnyAnywhere(monster, new SuperFieldAccessMatcher(AbstractCreature.class, "halfDead"));
    }

    default boolean actAtLeast(int num) {
        if (!CardCrawlGame.isInARun()) {
            return true;
        }
        return AbstractDungeon.actNum >= num;
    }

    default boolean actAtMost(int num) {
        if (!CardCrawlGame.isInARun()) {
            return true;
        }
        return AbstractDungeon.actNum <= num;
    }

    default int getConstructedBaseHealth(AbstractMonster monster) {
        return MonsterFields.baseMaxHP.get(monster);
    }

    default boolean playerHasACurse() {
        if (!CardCrawlGame.isInARun()) {
            return true; // for validity checks in a monster compendium if we make that
        }
        return AbstractDungeon.player.masterDeck.group.stream().anyMatch(c -> c.type == AbstractCard.CardType.CURSE);
    }

    default boolean playerCheck(Predicate<AbstractPlayer> p) {
        if (!CardCrawlGame.isInARun()) {
            return true;
        }
        return p.test(Wiz.adp());
    }

    default boolean doesntOverride(AbstractMonster monster, String method, Class<?>... paramtypez) {
        return ClassAnalyzer.doesntOverride(monster, AbstractMonster.class, method, paramtypez);
    }

    default boolean hasBlockTurn(AbstractMonster monster) {
        return ClassAnalyzer.methodHasAnyClass(monster, TAKE_TURN, GainBlockAction.class);
    }

    // TODO - Extra intents banned or rectified for reactive enemies? Recursive search?
    default boolean hasReactiveIntent(AbstractMonster monster) {
        return ClassAnalyzer.classHasAnyClass(monster, ReactivePower.class) ||
                ClassAnalyzer.classHasAnyClassWhere(monster, isNotTakeTurn, RollMoveAction.class) ||
                ClassAnalyzer.classHasAnyMatchers(monster, new Matcher.MethodCallMatcher(AbstractMonster.class, "createIntent"));
    }

    default boolean hasAnyInTurn(AbstractMonster monster, Class<?>... clazzez) {
        return ClassAnalyzer.methodHasAnyClass(monster, TAKE_TURN, clazzez);
    }

    default boolean hasAnyInTurn(AbstractMonster monster, Matcher... matchers) {
        return ClassAnalyzer.methodHasAnyMatchers(monster, TAKE_TURN, matchers);
    }

    default boolean hasAllInTurn(AbstractMonster monster, Class<?>... clazzez) {
        return ClassAnalyzer.methodHasAllClass(monster, TAKE_TURN, clazzez);
    }

    default boolean hasAllInTurn(AbstractMonster monster, Matcher... matchers) {
        return ClassAnalyzer.methodHasAllMatchers(monster, TAKE_TURN, matchers);
    }

    default boolean hasAnyNonTurn(AbstractMonster mon, Class<?>... clazzez) {
        return ClassAnalyzer.classHasAnyClassWhere(mon, isNotTakeTurn, clazzez);
    }

    default boolean hasAnyNonTurn(AbstractMonster mon, Matcher... matchers) {
        return ClassAnalyzer.classHasAnyMatchersWhere(mon, isNotTakeTurn, matchers);
    }

    default boolean hasAllNonTurn(AbstractMonster mon, Class<?>... clazzez) {
        return ClassAnalyzer.classHasAllClassWhere(mon, isNotTakeTurn, clazzez);
    }

    default boolean hasAllNonTurn(AbstractMonster mon, Matcher... matchers) {
        return ClassAnalyzer.classHasAllMatchersWhere(mon, isNotTakeTurn, matchers);
    }

    default boolean hasAnyInSetup(AbstractMonster monster, Class<?>... clazzez) {
        return ClassAnalyzer.methodHasAnyClass(monster, PRE_BATTLE, clazzez);
    }

    default boolean hasAnyInSetup(AbstractMonster monster, Matcher... matchers) {
        return ClassAnalyzer.methodHasAnyMatchers(monster, PRE_BATTLE, matchers);
    }

    default boolean hasAllInSetup(AbstractMonster monster, Class<?>... clazzez) {
        return ClassAnalyzer.methodHasAllClass(monster, PRE_BATTLE, clazzez);
    }

    default boolean hasAllInSetup(AbstractMonster monster, Matcher... matchers) {
        return ClassAnalyzer.methodHasAllMatchers(monster, PRE_BATTLE, matchers);
    }

    default boolean hasAnyNonSetup(AbstractMonster mon, Class<?>... clazzez) {
        return ClassAnalyzer.classHasAnyClassWhere(mon, isNotPreBattle, clazzez);
    }

    default boolean hasAnyNonSetup(AbstractMonster mon, Matcher... matchers) {
        return ClassAnalyzer.classHasAnyMatchersWhere(mon, isNotPreBattle, matchers);
    }

    default boolean hasAllNonSetup(AbstractMonster mon, Class<?>... clazzez) {
        return ClassAnalyzer.classHasAllClassWhere(mon, isNotPreBattle, clazzez);
    }

    default boolean hasAllNonSetup(AbstractMonster mon, Matcher... matchers) {
        return ClassAnalyzer.classHasAllMatchersWhere(mon, isNotPreBattle, matchers);
    }

    default boolean hasAnyAnywhere(Object o, Class<?>... clazzez) {
        return ClassAnalyzer.classHasAnyClass(o, clazzez);
    }

    default boolean hasAnyAnywhere(Object o, Matcher... matchers) {
        return ClassAnalyzer.classHasAnyMatchers(o, matchers);
    }

    default boolean hasAllAnywhere(Object o, Class<?>... clazzez) {
        return ClassAnalyzer.classHasAllClass(o, clazzez);
    }

    default boolean hasAllAnywhere(Object o, Matcher... matchers) {
        return ClassAnalyzer.classHasAllMatchers(o, matchers);
    }

    default boolean usesActionThat(Object o, Matcher... matchers) {
        for (CtMethod ctMethod : CtClassAnalyzer.ctMethodFinder(CtClassAnalyzer.ctOf(o))) {
            for (CtClass ctClass : CtClassAnalyzer.getReferencedClasses(ctMethod, Collections.singletonList(CtClassAnalyzer.ctOf(AbstractGameAction.class)))) {
                for (CtMethod refCtMethod : CtClassAnalyzer.ctMethodFinder(ctClass)) {
                    if (CtClassAnalyzer.performTest(refCtMethod, matchers)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    default boolean usesPowerThat(Object o, Matcher... matchers) {
        for (CtMethod ctMethod : CtClassAnalyzer.ctMethodFinder(CtClassAnalyzer.ctOf(o))) {
            for (CtClass ctClass : CtClassAnalyzer.getReferencedClasses(ctMethod, Collections.singletonList(CtClassAnalyzer.ctOf(AbstractPower.class)))) {
                for (CtMethod refCtMethod : CtClassAnalyzer.ctMethodFinder(ctClass)) {
                    if (CtClassAnalyzer.performTest(refCtMethod, matchers)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    default boolean usesActionThatRecursively(Object o, Matcher... matchers) {
        for (CtMethod ctMethod : CtClassAnalyzer.ctMethodFinder(CtClassAnalyzer.ctOf(o))) {
            for (CtClass ctClass : CtClassAnalyzer.getReferencedClasses(ctMethod, Collections.singletonList(CtClassAnalyzer.ctOf(AbstractGameAction.class)))) {
                if (CtClassAnalyzer.ctClassTester(ctClass, matchers)) {
                    return true;
                }
            }
        }
        return false;
    }

    default boolean usesPowerThatRecursively(Object o, Matcher... matchers) {
        for (CtMethod ctMethod : CtClassAnalyzer.ctMethodFinder(CtClassAnalyzer.ctOf(o))) {
            for (CtClass ctClass : CtClassAnalyzer.getReferencedClasses(ctMethod, Collections.singletonList(CtClassAnalyzer.ctOf(AbstractPower.class)))) {
                if (CtClassAnalyzer.ctClassTester(ctClass, matchers)) {
                    return true;
                }
            }
        }
        return false;
    }
}
