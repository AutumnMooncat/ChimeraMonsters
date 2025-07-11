package ChimeraMonsters.modifiers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.MonsterModifierFieldPatches;
import ChimeraMonsters.util.Wiz;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class AbstractMonsterModifier {
    private static final String[] BASE_TEXT = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID(AbstractMonsterModifier.class.getSimpleName())).TEXT;
    public static final float BUFF_HUGE = 3/2f;
    public static final float BUFF_MAJOR = 4/3f;
    public static final float BUFF_MODERATE = 5/4f;
    public static final float BUFF_MINOR = 6/5f;
    public static final float BUFF_TINY = 11/10f;
    public static final float DEBUFF_HUGE = 1/2f;
    public static final float DEBUFF_MAJOR = 2/3f;
    public static final float DEBUFF_MODERATE = 3/4f;
    public static final float DEBUFF_MINOR = 4/5f;
    public static final float DEBUFF_TINY = 9/10f;
    public static final Predicate<MonsterGroup> singleCombat = (group) -> group.monsters.size() == 1;
    public static final Predicate<MonsterGroup> multiCombat = (group) -> group.monsters.size() > 1;
    public static final BiPredicate<AbstractMonster, MonsterGroup> lastMonster = (mon, group) -> group.monsters.get(group.monsters.size() - 1) == mon;

    private static final ArrayList<AbstractCard> cardsToCheck = new ArrayList<>();

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

    public boolean checkContext(MonsterGroup context, Predicate<MonsterGroup> check) {
        return context == null || check.test(context);
    }

    public boolean checkContext(AbstractMonster monster, MonsterGroup context, BiPredicate<AbstractMonster, MonsterGroup> check) {
        return context == null || check.test(monster, context);
    }

    public void manipulateHealth(AbstractMonster monster, float factor) {
        monster.currentHealth = (int) (monster.currentHealth * factor);
        monster.maxHealth = (int) (monster.maxHealth * factor);
    }

    public void manipulateDamage(AbstractMonster monster, float factor) {
        for (DamageInfo di : monster.damage) {
            di.base = (int) (di.base * factor);
        }
    }

    public void manipulateBlock(AbstractMonster monster, float factor) {
        MonsterModifierFieldPatches.ModifierFields.blockMulti.set(monster, MonsterModifierFieldPatches.ModifierFields.blockMulti.get(monster) * factor);
    }

    public boolean hasThisMod(AbstractMonster monster) {
        return MonsterModifierFieldPatches.ModifierFields.receivedModifiers.get(monster).stream().anyMatch(mod -> mod.identifier().equals(identifier()));
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
        return doesntOverride(monster, AbstractMonster.class, method, paramtypez);
    }

    public static boolean doesntOverride(Object o, Class<?> clazz, String method, Class<?>... paramtypez) {
        try {
            return o.getClass().getMethod(method, paramtypez).getDeclaringClass().equals(clazz);
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

    public void addToBot(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    public void addToTop(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToTop(action);
    }
}
