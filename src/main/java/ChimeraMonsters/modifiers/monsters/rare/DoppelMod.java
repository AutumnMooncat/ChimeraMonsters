package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.DoppelPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class DoppelMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(DoppelMod.class.getSimpleName());

    public DoppelMod() {
        super(ID, ModifierRarity.RARE);
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
    public AbstractMonsterModifier makeCopy() {
        return new DoppelMod();
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
}
