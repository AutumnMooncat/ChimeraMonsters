package ChimeraMonsters.modifiers.monsters.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.*;

public class RegenerationMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(RegenerationMod.class.getSimpleName());

    public RegenerationMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return !hasAnyAnywhere(monster, RegenerateMonsterPower.class, RegenPower.class);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        int amount = scaleAbilities(monster, 1, 3) + AbstractDungeon.actNum * 3;

        applyPowersToCreature(monster, new RegenerateMonsterPower(monster, amount));
        startDamaged(monster, 0.7f);
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new RegenerationMod();
    }
}
