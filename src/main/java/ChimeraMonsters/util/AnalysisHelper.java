package ChimeraMonsters.util;

import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.patches.MonsterFields;
import ChimeraMonsters.util.analysis.ClassAnalyzer;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface AnalysisHelper {
    String TAKE_TURN = "takeTurn";
    String PRE_BATTLE = "usePreBattleAction";
    Predicate<MonsterGroup> singleCombat = (group) -> group.monsters.size() == 1;
    Predicate<MonsterGroup> multiCombat = (group) -> group.monsters.size() > 1;
    Predicate<MonsterGroup> noBosses = (group) -> group.monsters.stream().noneMatch(mon -> mon.type == AbstractMonster.EnemyType.BOSS);
    Predicate<MonsterGroup> noElites = (group) -> group.monsters.stream().noneMatch(mon -> mon.type == AbstractMonster.EnemyType.ELITE);
    Predicate<MonsterGroup> noElitesOrBosses = (group) -> group.monsters.stream().allMatch(mon -> mon.type == AbstractMonster.EnemyType.NORMAL);
    BiPredicate<MonsterGroup, AbstractMonsterModifier> onePerFight = (group, toCheck) -> group.monsters.stream().noneMatch(mon -> MonsterFields.receivedModifiers.get(mon).stream().anyMatch(mod -> mod.identifier().equals(toCheck.identifier())));
    BiPredicate<MonsterGroup, AbstractMonster> lastMonster = (group, mon) -> group.monsters.get(group.monsters.size() - 1) == mon;

    default boolean checkContext(MonsterGroup context, Predicate<MonsterGroup> check) {
        return context == null || check.test(context);
    }

    default boolean checkContext(MonsterGroup context, AbstractMonster monster, BiPredicate<MonsterGroup, AbstractMonster> check) {
        return context == null || check.test(context, monster);
    }

    default boolean checkContext(MonsterGroup context, AbstractMonsterModifier mod, BiPredicate<MonsterGroup, AbstractMonsterModifier> check) {
        return context == null || check.test(context, mod);
    }

    default boolean noEliteCheck(AbstractMonster monster, MonsterGroup context) {
        if (monster.type == AbstractMonster.EnemyType.ELITE) {
            return false;
        }
        if (!checkContext(context, noElites)) {
            return false;
        }
        if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom() == null) {
            return true;
        }
        return !(AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite);
    }

    default boolean noBossCheck(AbstractMonster monster, MonsterGroup context) {
        if (monster.type == AbstractMonster.EnemyType.BOSS) {
            return false;
        }
        if (!checkContext(context, noBosses)) {
            return false;
        }
        if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom() == null) {
            return true;
        }
        return !(AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss);
    }

    default boolean noEliteOrBossCheck(AbstractMonster monster, MonsterGroup context) {
        if (monster.type != AbstractMonster.EnemyType.NORMAL) {
            return false;
        }
        if (!checkContext(context, noElitesOrBosses)) {
            return false;
        }
        if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom() == null) {
            return true;
        }
        return !((AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite) || (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss));
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
}
