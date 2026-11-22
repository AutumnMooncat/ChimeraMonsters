package ChimeraMonsters.modifiers.monsters.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.groups.themed.SimpleThemedModifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.BloodlustPower;
import ChimeraMonsters.powers.VampiricPower;
import ChimeraMonsters.util.matchers.OverrideMatcher;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.powers.IntangiblePower;

public class VampiricMod extends AbstractMonsterModifier implements SimpleThemedModifier {
    public static final String ID = ChimeraMonstersMod.makeID(VampiricMod.class.getSimpleName());
    public static final int PERCENTAGE = 50;

    public VampiricMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        if (doesScale.test(monster) || hasAnyAnywhere(monster, IntangiblePlayerPower.class, IntangiblePower.class)) {
            return false;
        }
        return !usesPowerThat(monster, new OverrideMatcher(AbstractPower.class, "onInflictDamage"));
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new VampiricPower(monster, PERCENTAGE));
        int blood = monster.type == AbstractMonster.EnemyType.BOSS ? 2 : 1;
        applyPowersToCreature(monster, new BloodlustPower(monster, scaleAbilities(monster, blood, blood + 1)));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new VampiricMod();
    }
}
