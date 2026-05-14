package ChimeraMonsters.modifiers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.MonsterFields;
import ChimeraMonsters.util.Wiz;
import ChimeraMonsters.util.analysis.ClassAnalyzer;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class AbstractMonsterModifier {
    private static final String[] BASE_TEXT = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID(AbstractMonsterModifier.class.getSimpleName())).TEXT;
    public static final float BUFF_50 = 3 / 2f;
    public static final float BUFF_33 = 4 / 3f;
    public static final float BUFF_25 = 5 / 4f;
    public static final float BUFF_20 = 6 / 5f;
    public static final float BUFF_10 = 11 / 10f;
    public static final float DEBUFF_50 = 1 / 2f;
    public static final float DEBUFF_33 = 2 / 3f;
    public static final float DEBUFF_25 = 3 / 4f;
    public static final float DEBUFF_20 = 4 / 5f;
    public static final float DEBUFF_10 = 9 / 10f;
    public static final Predicate<MonsterGroup> singleCombat = (group) -> group.monsters.size() == 1;
    public static final Predicate<MonsterGroup> multiCombat = (group) -> group.monsters.size() > 1;
    public static final BiPredicate<MonsterGroup, AbstractMonsterModifier> onePerFight = (group, toCheck) -> group.monsters.stream().noneMatch(mon -> MonsterFields.receivedModifiers.get(mon).stream().anyMatch(mod -> mod.identifier().equals(toCheck.identifier())));
    public static final BiPredicate<MonsterGroup, AbstractMonster> lastMonster = (group, mon) -> group.monsters.get(group.monsters.size() - 1) == mon;

    private static final ArrayList<AbstractCard> cardsToCheck = new ArrayList<>();
    private static final String TAKE_TURN = "takeTurn";
    private static final String PRE_BATTLE = "usePreBattleAction";

    public enum ModifierRarity {
        COMMON,
        UNCOMMON,
        RARE,
        SPECIAL;

        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    public abstract ModifierRarity getModRarity();

    protected abstract boolean validMonster(AbstractMonster monster, MonsterGroup context);

    public abstract void applyTo(AbstractMonster monster);

    public void applyPowersToCreature(AbstractCreature owner, AbstractPower... powers) {
        for (AbstractPower powerToApply : powers) {
            AbstractPower p = owner.getPower(powerToApply.ID);
            if (p != null) {
                p.stackPower(powerToApply.amount);
                if (!(p instanceof InvisiblePower)) {
                    p.flash();
                }
                p.updateDescription();
            } else {
                owner.addPower(powerToApply);
                Collections.sort(owner.powers);
                powerToApply.onInitialApplication();
                if (!(powerToApply instanceof InvisiblePower)) {
                    powerToApply.flash();
                }
            }
            //AbstractDungeon.onModifyPower();
        }
        //TODO: Apply Buff/Debuff VFX
    }

    public abstract String identifier();

    public abstract AbstractMonsterModifier makeCopy();

    public abstract String getPrefix();

    public abstract String getSuffix();

    public abstract String getModifierDescription();

    public String getModifierName() {
        String s = (getPrefix() + getSuffix()).replace("  ", " ").trim();
        if (s.isEmpty()) {
            s = getClass().getSimpleName();
        }
        return s;
    }

    public String modifyName(AbstractMonster monster) {
        return getPrefix() + monster.name + getSuffix();
    }

    public <T> T scaleDeadlier(AbstractMonster monster, T base, T boosted) {
        switch (monster.type) {
            case BOSS:
                return AbstractDungeon.ascensionLevel >= 4 ? boosted : base;
            case ELITE:
                return AbstractDungeon.ascensionLevel >= 3 ? boosted : base;
            default:
                return AbstractDungeon.ascensionLevel >= 2 ? boosted : base;
        }
    }

    public <T extends Number> Number scaleDeadlier(AbstractMonster monster, T baseMin, T baseMax, T boostedMin, T boostedMax) {
        T min, max;
        switch (monster.type) {
            case BOSS:
                min = AbstractDungeon.ascensionLevel >= 4 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 4 ? boostedMax : baseMax;
                break;
            case ELITE:
                min = AbstractDungeon.ascensionLevel >= 3 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 3 ? boostedMax : baseMax;
                break;
            default:
                min = AbstractDungeon.ascensionLevel >= 2 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 2 ? boostedMax : baseMax;
        }
        if (min instanceof Float || min instanceof Double || max instanceof Float || max instanceof Double) {
            return AbstractDungeon.monsterHpRng.random(min.floatValue(), max.floatValue());
        }
        return AbstractDungeon.monsterHpRng.random(min.longValue(), max.longValue());
    }

    public <T> T scaleTougher(AbstractMonster monster, T base, T boosted) {
        switch (monster.type) {
            case BOSS:
                return AbstractDungeon.ascensionLevel >= 9 ? boosted : base;
            case ELITE:
                return AbstractDungeon.ascensionLevel >= 8 ? boosted : base;
            default:
                return AbstractDungeon.ascensionLevel >= 7 ? boosted : base;
        }
    }

    public <T extends Number> Number scaleTougher(AbstractMonster monster, T baseMin, T baseMax, T boostedMin, T boostedMax) {
        T min, max;
        switch (monster.type) {
            case BOSS:
                min = AbstractDungeon.ascensionLevel >= 9 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 9 ? boostedMax : baseMax;
                break;
            case ELITE:
                min = AbstractDungeon.ascensionLevel >= 8 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 8 ? boostedMax : baseMax;
                break;
            default:
                min = AbstractDungeon.ascensionLevel >= 7 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 7 ? boostedMax : baseMax;
        }
        if (min instanceof Float || min instanceof Double || max instanceof Float || max instanceof Double) {
            return AbstractDungeon.monsterHpRng.random(min.floatValue(), max.floatValue());
        }
        return AbstractDungeon.monsterHpRng.random(min.longValue(), max.longValue());
    }

    public <T> T scaleAbilities(AbstractMonster monster, T base, T boosted) {
        switch (monster.type) {
            case BOSS:
                return AbstractDungeon.ascensionLevel >= 19 ? boosted : base;
            case ELITE:
                return AbstractDungeon.ascensionLevel >= 18 ? boosted : base;
            default:
                return AbstractDungeon.ascensionLevel >= 17 ? boosted : base;
        }
    }

    public <T extends Number> Number scaleAbilities(AbstractMonster monster, T baseMin, T baseMax, T boostedMin, T boostedMax) {
        T min, max;
        switch (monster.type) {
            case BOSS:
                min = AbstractDungeon.ascensionLevel >= 19 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 19 ? boostedMax : baseMax;
                break;
            case ELITE:
                min = AbstractDungeon.ascensionLevel >= 18 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 18 ? boostedMax : baseMax;
                break;
            default:
                min = AbstractDungeon.ascensionLevel >= 17 ? boostedMin : baseMin;
                max = AbstractDungeon.ascensionLevel >= 17 ? boostedMax : baseMax;
        }
        if (min instanceof Float || min instanceof Double || max instanceof Float || max instanceof Double) {
            return AbstractDungeon.monsterHpRng.random(min.floatValue(), max.floatValue());
        }
        return AbstractDungeon.monsterHpRng.random(min.longValue(), max.longValue());
    }

    public boolean checkContext(MonsterGroup context, Predicate<MonsterGroup> check) {
        return context == null || check.test(context);
    }

    public boolean checkContext(MonsterGroup context, AbstractMonster monster, BiPredicate<MonsterGroup, AbstractMonster> check) {
        return context == null || check.test(context, monster);
    }

    public boolean checkContext(MonsterGroup context, BiPredicate<MonsterGroup, AbstractMonsterModifier> check) {
        return context == null || check.test(context, this);
    }

    public void manipulateBaseHealth(AbstractMonster monster, float factor) {
        monster.currentHealth = Math.max(1,(int) (monster.currentHealth * factor));
        monster.maxHealth = Math.max(1,(int) (monster.maxHealth * factor));
    }

    public void startDamaged(AbstractMonster monster, float factor){
        monster.currentHealth = Math.max(1, Math.min(monster.currentHealth,(int) (monster.maxHealth * factor)));
    }

    public void manipulateBaseDamage(AbstractMonster monster, float factor) {
        //TODO: what to do about Hexaghost Turn 2 and similar attacks
        for (DamageInfo di : monster.damage) {
            di.base = (int) (di.base * factor);
        }
    }

    public void manipulateFinalBlock(AbstractMonster monster, float factor) {
        MonsterFields.blockMulti.set(monster, MonsterFields.blockMulti.get(monster) * factor);
    }

    public boolean hasThisMod(AbstractMonster monster) {
        return MonsterFields.receivedModifiers.get(monster).stream().anyMatch(mod -> mod.identifier().equals(identifier()));
    }

    public boolean canApplyTo(AbstractMonster monster, MonsterGroup context) {
        if (monster != null && !hasThisMod(monster) && !ChimeraMonstersMod.customBanChecks.getOrDefault(identifier(), c -> false).test(monster)) {
            return validMonster(monster, context);
        }
        return false;
    }

    public static boolean playerHasACurse() {
        if (!CardCrawlGame.isInARun()) {
            return true; // for validity checks in a monster compendium if we make that
        }
        return AbstractDungeon.player.masterDeck.group.stream().anyMatch(c -> c.type == AbstractCard.CardType.CURSE);
    }

    public static boolean playerCheck(Predicate<AbstractPlayer> p) {
        if (!CardCrawlGame.isInARun()) {
            return true;
        }
        return p.test(Wiz.adp());
    }

    public static boolean doesntOverride(AbstractMonster monster, String method, Class<?>... paramtypez) {
        return ClassAnalyzer.doesntOverride(monster, AbstractMonster.class, method, paramtypez);
    }

    public static boolean hasBlockTurn(AbstractMonster monster) {
        return ClassAnalyzer.methodHasAnyClass(monster, TAKE_TURN, GainBlockAction.class);
    }

    public static boolean hasAnyInTurn(AbstractMonster monster, Class<?>... clazzez) {
        return ClassAnalyzer.methodHasAnyClass(monster, TAKE_TURN, clazzez);
    }

    public static boolean hasAllInTurn(AbstractMonster monster, Class<?>... clazzez) {
        return ClassAnalyzer.methodHasAllClass(monster, TAKE_TURN, clazzez);
    }

    public static boolean hasAnyInSetup(AbstractMonster monster, Class<?>... clazzez) {
        return ClassAnalyzer.methodHasAnyClass(monster, PRE_BATTLE, clazzez);
    }

    public static boolean hasAllInSetup(AbstractMonster monster, Class<?>... clazzez) {
        return ClassAnalyzer.methodHasAllClass(monster, PRE_BATTLE, clazzez);
    }

    public static boolean hasAnyAnywhere(AbstractMonster monster, Class<?>... clazzez) {
        return ClassAnalyzer.classHasAnyClass(monster, clazzez);
    }

    public static boolean hasAllAnywhere(AbstractMonster monster, Class<?>... clazzez) {
        return ClassAnalyzer.classHasAllClass(monster, clazzez);
    }

    public void addToBot(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    public void addToTop(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToTop(action);
    }
}
