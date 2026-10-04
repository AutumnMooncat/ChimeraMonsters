package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.*;

public class ArmoredMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(ArmoredMod.class.getSimpleName());

    public ArmoredMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        if (hasAnyAnywhere(monster, PlatedArmorPower.class, BarricadePower.class, MetallicizePower.class, ThornsPower.class)) {
            return false;
        }
        return hasBlockTurn(monster) && noBossCheck(monster, context) && actAtMost(3);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new BarricadePower(monster));
        monster.addBlock(scaleTougher(monster, 6, 8) * AbstractDungeon.actNum);
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new ArmoredMod();
    }
}
