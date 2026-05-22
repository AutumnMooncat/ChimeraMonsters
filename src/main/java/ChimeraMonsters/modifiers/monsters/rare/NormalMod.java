package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.NormalPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class NormalMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(NormalMod.class.getSimpleName());

    public NormalMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noEliteOrBossCheck(monster, context);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseHealth(monster, DEBUFF_33);
        applyPowersToCreature(monster, new NormalPower(monster, 1));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new NormalMod();
    }
}
