package ChimeraMonsters.modifiers.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.modifiers.GroupMonsterModifier;
import ChimeraMonsters.powers.DoppelPower;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class DoppelMod extends GroupMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(DoppelMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public boolean isMonsterGroupValid(MonsterGroup monsterGroup) {
        return true;
    }

    @Override
    public String fightName(MonsterGroup context) {
        return "Doppeling"; //TODO: WIP. Like the Noun thing tho
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
        manipulateBaseDamage(monster, 0.5f);
        applyPowersToCreature(monster, new DoppelPower(monster, 1));
    }

    @Override
    public String identifier() {
        return ID;
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new DoppelMod();
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
    public String modifyName(AbstractMonster monster) {
        String[] nameWords = monster.name.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nameWords.length-1; i++) {
            sb.append(nameWords[i]);
            sb.append(" ");
        }
        sb.append(getPrefix());
        sb.append(nameWords[nameWords.length-1].toLowerCase());
        sb.append(getSuffix());
        return sb.toString();

    }

    @Override
    public String getModifierDescription() {
        return TEXT[2];
    }
}
