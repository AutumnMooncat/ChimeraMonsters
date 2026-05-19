package ChimeraMonsters.modifiers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.MonsterFields;
import ChimeraMonsters.util.AscensionScaling;
import ChimeraMonsters.util.AnalysisHelper;
import ChimeraMonsters.util.MonsterManipulator;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.*;

public abstract class AbstractMonsterModifier implements AscensionScaling, AnalysisHelper, MonsterManipulator {
    private static final String[] BASE_TEXT = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID(AbstractMonsterModifier.class.getSimpleName())).TEXT;

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

    public boolean hasThisMod(AbstractMonster monster) {
        return MonsterFields.receivedModifiers.get(monster).stream().anyMatch(mod -> mod.identifier().equals(identifier()));
    }

    public boolean canApplyTo(AbstractMonster monster, MonsterGroup context) {
        if (monster != null && !hasThisMod(monster) && !ChimeraMonstersMod.customBanChecks.getOrDefault(identifier(), c -> false).test(monster)) {
            return validMonster(monster, context);
        }
        return false;
    }

    public void addToBot(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    public void addToTop(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToTop(action);
    }
}
