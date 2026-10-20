package ChimeraMonsters.modifiers.monsters;

import ChimeraMonsters.ChimeraMonstersConfig;
import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.Modifier;
import ChimeraMonsters.patches.MonsterFields;
import ChimeraMonsters.util.AscensionScaling;
import ChimeraMonsters.util.AnalysisHelper;
import ChimeraMonsters.util.MonsterManipulator;
import ChimeraMonsters.util.Wiz;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public abstract class AbstractMonsterModifier implements Modifier<AbstractMonster>, AscensionScaling, AnalysisHelper, MonsterManipulator {
    private static final String[] BASE_TEXT = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID(AbstractMonsterModifier.class.getSimpleName())).TEXT;
    protected String modifierID;
    protected UIStrings uiStrings;
    protected ModifierRarity rarity;

    public AbstractMonsterModifier(String ID, ModifierRarity rarity) {
        this.modifierID = ID;
        this.uiStrings = CardCrawlGame.languagePack.getUIString(ID);
        this.rarity = rarity;

        if (uiStrings == null) {
            throw new NullPointerException("Could not find uiStrings for "+ID);
        }
    }

    @Override
    public ModifierRarity getModRarity() {
        return rarity;
    }

    protected abstract boolean validMonster(AbstractMonster monster, MonsterGroup context);

    /**
     * Called when the modifier is rolled on the monster.
     * Use for changes that need to take place immediately, before monster has called init().
     * We do not want to use actions here, and no preBattleActions have run yet, so powers can be applied here via applyPowersToCreature().
     * Most powers can be applied without issue this way, but care should be taken if they interact with other powers,
     * as the inherent powers of the monster such as Slow have not been applied yet.
     * @param target The target to modify
     */
    @Override
    public abstract void applyTo(AbstractMonster target);

    /**
     * Called after the monster's preBattleActions have been added to the action queue.
     * Use for applying powers normally and any other actions that need to run before the battle.
     * @param monster The target to modify
     * @param context The current MonsterGroup
     */
    public void preBattleActions(AbstractMonster monster, MonsterGroup context) {}

    @Override
    public String identifier() {
        return modifierID;
    }

    public UIStrings getStrings() {
        return uiStrings;
    }

    public String getPrefix() {
        return uiStrings.TEXT[0];
    }

    public String getSuffix() {
        return uiStrings.TEXT[1];
    }

    public String getModifierName() {
        String s = (getPrefix() + getSuffix()).replace("  ", " ").trim();
        if (s.isEmpty()) {
            s = getClass().getSimpleName();
        }
        return s;
    }

    @Override
    public String getModifierDescription() {
        return uiStrings.TEXT[2];
    }

    public String modifyName(AbstractMonster monster) {
        return getPrefix() + monster.name + getSuffix();
    }

    public boolean hasThisMod(AbstractMonster monster) {
        return MonsterFields.receivedModifiers.get(monster).stream().anyMatch(mod -> mod.identifier().equals(identifier()));
    }

    @Override
    public boolean canApplyTo(AbstractMonster target) {
        return canApplyTo(target, null);
    }

    public boolean canApplyTo(AbstractMonster monster, MonsterGroup context) {
        if (monster != null && !hasThisMod(monster) && !ChimeraMonstersConfig.customBanChecks.getOrDefault(identifier(), c -> false).test(monster)) {
            return validMonster(monster, context);
        }
        return false;
    }

    public void addToBot(AbstractGameAction... actions) {
        Wiz.sequenceActions(false, actions);
    }

    public void addToTop(AbstractGameAction... actions) {
        Wiz.sequenceActions(true, actions);
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        try {
            return getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Chimera Monsters failed to make copy of " + getClass().getName());
        }
    }
}
