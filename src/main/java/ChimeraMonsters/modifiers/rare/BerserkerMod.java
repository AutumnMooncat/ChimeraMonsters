package ChimeraMonsters.modifiers.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.powers.BerserkerPower;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class BerserkerMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(BerserkerMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

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
        return checkContext(context, multiCombat) && checkContext(context, monster, lastMonster) && monster.type == AbstractMonster.EnemyType.NORMAL;
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new BerserkerPower(monster, 3));
    }

    @Override
    public String identifier() {
        return ID;
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new BerserkerMod();
    }
}
