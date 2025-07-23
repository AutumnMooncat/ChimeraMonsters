package ChimeraMonsters.modifiers.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.modifiers.GroupMonsterModifier;
import ChimeraMonsters.powers.TwinPower;
import ChimeraMonsters.util.Wiz;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class TwinMod extends GroupMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(TwinMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public boolean isMonsterGroupValid(MonsterGroup monsterGroup) {
        return true;
    }

    @Override
    public String fightName(MonsterGroup context) {
        return "Windfury " + context.monsters.get(0).name + "s";
    }

    @Override
    public ModifierRarity getModRarity() {
        return ModifierRarity.RARE;
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return true;
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        Wiz.applyToEnemy(monster, new TwinPower(monster, 1));
    }

    @Override
    public String identifier() {
        return ID;
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new TwinMod();
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
