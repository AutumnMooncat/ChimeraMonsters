package ChimeraMonsters.modifiers.monsters.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.DilatantPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class DilatantMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(DilatantMod.class.getSimpleName());

    public DilatantMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noEliteOrBossCheck(monster, context) && checkContext(context, multiCombat);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new DilatantPower(monster));
        manipulateBaseHealth(monster, DEBUFF_20);
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new DilatantMod();
    }
}
