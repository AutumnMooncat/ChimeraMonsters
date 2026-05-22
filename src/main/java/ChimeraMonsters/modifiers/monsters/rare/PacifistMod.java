package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.PacifistPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class PacifistMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(PacifistMod.class.getSimpleName());

    public PacifistMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return checkContext(context, multiCombat) && checkContext(context, this, onePerFight);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new PacifistPower(monster, AbstractDungeon.actNum * 5));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new PacifistMod();
    }
}
