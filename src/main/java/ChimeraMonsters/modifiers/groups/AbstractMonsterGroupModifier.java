package ChimeraMonsters.modifiers.groups;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.Modifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.util.AnalysisHelper;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public abstract class AbstractMonsterGroupModifier implements Modifier<MonsterGroup>, AnalysisHelper {
    private static final String[] BASE_TEXT = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID(AbstractMonsterModifier.class.getSimpleName())).TEXT;
    protected String modifierID;
    protected UIStrings uiStrings;

    public AbstractMonsterGroupModifier(String ID) {
        this.modifierID = ID;
        this.uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    }

    protected abstract boolean validGroup(MonsterGroup group);

    public abstract void applyTo(MonsterGroup group);

    public String identifier() {
        return modifierID;
    }

    public String getModifierName() {
        return uiStrings.TEXT[0];
    }

    public String getModifierDescription() {
        return uiStrings.TEXT[1];
    }

    public boolean canApplyTo(MonsterGroup group) {
        return validGroup(group);
    }

    public boolean allCanAccept(MonsterGroup group, AbstractMonsterModifier mod) {
        return group.monsters.stream().allMatch(mon -> mod.canApplyTo(mon, group));
    }

    @Override
    public AbstractMonsterGroupModifier makeCopy() {
        try {
            return getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Chimera Monsters failed to make copy of " + getClass().getName());
        }
    }
}
