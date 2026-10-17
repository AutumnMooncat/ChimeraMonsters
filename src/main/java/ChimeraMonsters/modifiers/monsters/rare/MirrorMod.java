package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.MirrorPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class MirrorMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(MirrorMod.class.getSimpleName());

    public MirrorMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noEliteOrBossCheck(monster, context);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new MirrorPower(monster));
        manipulateBaseDamage(monster, DEBUFF_33);
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new MirrorMod();
    }
}
