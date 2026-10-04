package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.CrackedPower;
import ChimeraMonsters.powers.IcyPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class IcyMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(IcyMod.class.getSimpleName());

    public IcyMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noEliteOrBossCheck(monster, context);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new IcyPower(monster, scaleAbilities(monster, 2, 3) * AbstractDungeon.actNum));
        applyPowersToCreature(monster, new CrackedPower(monster, 1));
        manipulateBaseHealth(monster, BUFF_50);
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new IcyMod();
    }
}
