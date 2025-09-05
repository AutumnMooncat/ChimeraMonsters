package ChimeraMonsters.util.analysis;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.BranchingUpgradesCard;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.MultiUpgradeCard;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiPredicate;

public class CardAnalyzer {
    private static final HashMap<String, ArrayList<AbstractCard>> cachedUpgrades = new HashMap<>();
    public static String[] orbCodes = {"[E]","[R]","[B]","[G]","[W]"};

    public static boolean hasKeyword(AbstractCard card, String[] keywords) {
        return Arrays.stream(keywords).anyMatch(s -> card.keywords.contains(s));
    }

    public static boolean hasOrb(AbstractCard card) {
        return Arrays.stream(orbCodes).anyMatch(s -> CardModifierManager.onCreateDescription(card, card.rawDescription).contains(s));
    }

    private static ArrayList<AbstractCard> getCardStates(AbstractCard card) {
        if (cachedUpgrades.containsKey(card.cardID)) {
            return cachedUpgrades.get(card.cardID);
        }
        ArrayList<AbstractCard> cardsToCheck = new ArrayList<>();
        //Grab an unmodified copy of the card.
        AbstractCard baseCheck = CardLibrary.getCard(card.cardID);
        if (baseCheck == null) {
            //If we don't have this in the library, use a blank copy.
            baseCheck = card.makeCopy();
        } else {
            //Make a copy to ensure we never modify the card library version.
            baseCheck = baseCheck.makeCopy();
        }
        cardsToCheck.add(baseCheck);
        if (card instanceof BranchingUpgradesCard) {
            //If this is branching upgrade card we need both the upgrade paths
            try {
                AbstractCard normalCheck = card.makeCopy();
                ((BranchingUpgradesCard) normalCheck).setUpgradeType(BranchingUpgradesCard.UpgradeType.NORMAL_UPGRADE);
                normalCheck.upgrade();
                cardsToCheck.add(normalCheck);
                AbstractCard branchCheck = card.makeCopy();
                ((BranchingUpgradesCard) branchCheck).setUpgradeType(BranchingUpgradesCard.UpgradeType.BRANCH_UPGRADE);
                branchCheck.upgrade();
                cardsToCheck.add(branchCheck);
            } catch (Exception ignored) {}
        } else if (card instanceof MultiUpgradeCard) {
            //Else if this is a multi upgrade card we need to check each individual upgrade.
            //We cant use the normal method of upgrading as upgrades can have dependencies, but we can force each upgrade index and test that way
            //Notably, we need to check the upgrades of the baseCheck in case something modified the amount of upgrades of the main card
            try {
                for (int i = 0 ; i < ((MultiUpgradeCard) baseCheck).getUpgrades().size() ; i++) {
                    AbstractCard upgradeTest = card.makeCopy();
                    ((MultiUpgradeCard)upgradeTest).getUpgrades().get(i).upgrade();
                    cardsToCheck.add(upgradeTest);
                }
            } catch (Exception ignored) {}
        } else {
            //Else its very simple
            try {
                AbstractCard upgradeCheck = card.makeCopy();
                upgradeCheck.upgrade();
                cardsToCheck.add(upgradeCheck);
            } catch (Exception ignored) {}
        }
        cachedUpgrades.put(card.cardID, cardsToCheck);
        return cardsToCheck;
    }

    public static boolean cardCheck(AbstractCard card, BiPredicate<AbstractCard, ArrayList<AbstractCard>> p) {
        boolean ret = false;
        try {
            ret = p.test(card, getCardStates(card));
        } catch (Exception ignored) {}
        return ret;
    }

    public static boolean triggersOnDraw(AbstractCard card) {
        if (ClassAnalyzer.overridesMethod(card, AbstractCard.class, "triggerWhenDrawn")) {
            return true;
        }
        for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
            if (ClassAnalyzer.overridesMethod(mod, AbstractCardModifier.class, "onDrawn", AbstractCard.class)) {
                return true;
            }
        }
        return false;
    }

    public static boolean triggersOnDiscard(AbstractCard card) {
        return ClassAnalyzer.overridesMethod(card, AbstractCard.class, "triggerOnManualDiscard");
    }

    public static boolean testCard(AbstractCard card, Matcher... matchers) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        classes.add(card.getClass());
        for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
            classes.add(mod.getClass());
        }
        for (AbstractDamageModifier dmod : DamageModifierManager.modifiers(card)) {
            classes.add(dmod.getClass());
        }
        return CtClassAnalyzer.testClasses(classes, matchers);
    }
}
