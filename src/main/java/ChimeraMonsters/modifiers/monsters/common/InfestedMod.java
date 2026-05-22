package ChimeraMonsters.modifiers.monsters.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.SporeCloudPower;

public class InfestedMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(InfestedMod.class.getSimpleName());

    public InfestedMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return checkContext(context, multiCombat);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseHealth(monster, DEBUFF_10);
        applyPowersToCreature(monster, new SporeCloudPower(monster, 2));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new InfestedMod();
    }
}
