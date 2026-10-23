package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.groups.themed.SimpleThemedModifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.HyperPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class HyperMod extends AbstractMonsterModifier implements SimpleThemedModifier {
    public static final String ID = ChimeraMonstersMod.makeID(HyperMod.class.getSimpleName());

    public HyperMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        if (getConstructedBaseHealth(monster) >= 500) {
            return false;
        }
        return noEliteOrBossCheck(monster, context);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseHealth(monster, DEBUFF_33);
        applyPowersToCreature(monster, new HyperPower(monster, 0));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new HyperMod();
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
