package ChimeraMonsters.modifiers.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.modifiers.GroupMonsterModifier;
import ChimeraMonsters.powers.VampiricPower;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.RegenerateMonsterPower;

public class RegenerationMod extends GroupMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(RegenerationMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final int ACT_ONE = 4;
    public static final int ACT_TWO = 7;
    public static final int ACT_THREE_PLUS = 10;

    @Override
    public ModifierRarity getModRarity() {
        return ModifierRarity.COMMON;
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
        return true; //TODO: Regeneration Check
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        int amount = 0;
        switch (AbstractDungeon.actNum){
            case 1:
                amount=ACT_ONE;
                break;
            case 2:
                amount=ACT_TWO;
                break;
            case 3:
            default:
                amount=ACT_THREE_PLUS;
        }

        applyPowersToCreature(monster, new RegenerateMonsterPower(monster, amount));
        startDamaged(monster, 0.7f);
    }

    @Override
    public String identifier() {
        return ID;
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new RegenerationMod();
    }

    @Override
    public boolean isMonsterGroupValid(MonsterGroup monsterGroup){
        for(AbstractMonster m : monsterGroup.monsters){
            if(!validMonster(m, monsterGroup)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String fightName(MonsterGroup context) {
        return "Rejuvenating Spring"; //TODO: localization
    }
}
