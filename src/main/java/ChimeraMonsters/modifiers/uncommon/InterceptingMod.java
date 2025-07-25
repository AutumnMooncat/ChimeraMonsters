package ChimeraMonsters.modifiers.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.powers.InterceptionPower;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class InterceptingMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(InterceptingMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public ModifierRarity getModRarity() {
        return ModifierRarity.UNCOMMON;
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
        return checkContext(context, multiCombat) && checkContext(context, onePerFight);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new InterceptionPower(monster, 10));
    }

    @Override
    public String identifier() {
        return ID;
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new InterceptingMod();
    }
}
