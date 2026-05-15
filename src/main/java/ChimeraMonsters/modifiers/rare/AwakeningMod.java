package ChimeraMonsters.modifiers.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.powers.AwakeningPower;
import ChimeraMonsters.util.matchers.SuperFieldAccessMatcher;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.UnawakenedPower;

public class AwakeningMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(AwakeningMod.class.getSimpleName());
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
        return monster.type == AbstractMonster.EnemyType.ELITE &&
                !hasAnyAnywhere(monster, new SuperFieldAccessMatcher(AbstractCreature.class, "halfDead"));
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseHealth(monster, DEBUFF_50);
        applyPowersToCreature(monster, new UnawakenedPower(monster));
        applyPowersToCreature(monster, new AwakeningPower(monster));
    }

    @Override
    public String identifier() {
        return ID;
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new AwakeningMod();
    }
}
