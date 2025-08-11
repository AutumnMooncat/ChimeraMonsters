package ChimeraMonsters.modifiers.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.modifiers.GroupMonsterModifier;
import ChimeraMonsters.powers.DoppelPower;
import ChimeraMonsters.powers.ShadowPower;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class ShadowMod extends GroupMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(ShadowMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public boolean isMonsterGroupValid(MonsterGroup monsterGroup) {
        return true;
    }

    @Override
    public String fightName(MonsterGroup context) {
        return "Shadelings"; //TODO: WIP. Like the Noun thing tho
    }

    @Override
    public ModifierRarity getModRarity() {
        return ModifierRarity.UNCOMMON;
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return true;
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseDamage(monster, BUFF_HUGE);
        manipulateBaseHealth(monster, DEBUFF_MAJOR);
        applyPowersToCreature(monster, new ShadowPower(monster, 1));
    }

    @Override
    public String identifier() {
        return ID;
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new ShadowMod();
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
}
