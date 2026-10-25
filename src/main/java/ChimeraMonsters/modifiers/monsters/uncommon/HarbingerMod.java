package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.HarbingerPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class HarbingerMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(HarbingerMod.class.getSimpleName());

    public HarbingerMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        if (getConstructedBaseHealth(monster) >= 100) {
            return false;
        }
        return checkContext(context, multiCombat) && checkContext(context, this, onePerFight);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new HarbingerPower(monster, 5));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new HarbingerMod();
    }
}
