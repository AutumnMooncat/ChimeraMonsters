package ChimeraMonsters.modifiers.monsters;

import ChimeraMonsters.ChimeraMonstersConfig;
import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.Modifier;
import ChimeraMonsters.patches.MonsterFields;
import ChimeraMonsters.util.AscensionScaling;
import ChimeraMonsters.util.AnalysisHelper;
import ChimeraMonsters.util.MonsterManipulator;
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
    }

    @Override
    public ModifierRarity getModRarity() {
        return rarity;
    }

    protected abstract boolean validMonster(AbstractMonster monster, MonsterGroup context);

    @Override
    public String identifier() {
        return modifierID;
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

    @Override
    public AbstractMonsterModifier makeCopy() {
        try {
            return getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Chimera Monsters failed to make copy of " + getClass().getName());
        }
    }
}
