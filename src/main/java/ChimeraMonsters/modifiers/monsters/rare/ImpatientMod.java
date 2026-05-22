package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.SlowPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.TimeWarpPower;

public class ImpatientMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(ImpatientMod.class.getSimpleName());

    public ImpatientMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return monster.type == AbstractMonster.EnemyType.ELITE && !hasAnyAnywhere(monster, SlowPower.class);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new TimeWarpPower(monster));
        if (scaleAbilities(monster, true, false)) {
            applyPowersToCreature(monster, new StrengthPower(monster, -2));
        }
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new ImpatientMod();
    }
}
