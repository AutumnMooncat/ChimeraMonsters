package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.ExplodingPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.*;

public class ExplodingMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(ExplodingMod.class.getSimpleName());

    public ExplodingMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        if (getConstructedBaseHealth(monster) >= 30) {
            return false;
        }
        return !hasAnyAnywhere(monster, FlightPower.class);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new ExplodingPower(monster, 3, AbstractDungeon.actNum * 10));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new ExplodingMod();
    }
}
