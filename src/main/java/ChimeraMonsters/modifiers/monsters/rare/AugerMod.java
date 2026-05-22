package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.AugerPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class AugerMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(AugerMod.class.getSimpleName());
    public static final int AMOUNT = 1;

    public AugerMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return checkContext(context, singleCombat);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseHealth(monster, DEBUFF_20);
        applyPowersToCreature(monster, new AugerPower(monster, AMOUNT));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new AugerMod();
    }
}
