package ChimeraMonsters.modifiers.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.powers.MonsterDexterityPower;
import ChimeraMonsters.util.Wiz;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class AggressiveMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(AggressiveMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final int AMOUNT = 1;

    @Override
    public ModifierRarity getModRarity() {
        return ModifierRarity.RARE;
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSuffix() {
        return TEXT[1];
    }

    @Override
    public String getModifierDescription() {
        return TEXT[2];
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return hasBlockAction(monster);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        Wiz.applyToEnemy(monster, new StrengthPower(monster, AMOUNT));
        Wiz.applyToEnemy(monster, new MonsterDexterityPower(monster, -AMOUNT));
    }

    @Override
    public String identifier() {
        return ID;
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new AggressiveMod();
    }
}
