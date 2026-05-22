package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.AwakeningPower;
import ChimeraMonsters.util.matchers.SuperFieldAccessMatcher;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.UnawakenedPower;

public class AwakeningMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(AwakeningMod.class.getSimpleName());

    public AwakeningMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return monster.type == AbstractMonster.EnemyType.ELITE &&
                !hasAnyAnywhere(monster, new SuperFieldAccessMatcher(AbstractCreature.class, "halfDead"));
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseHealth(monster, DEBUFF_50);
        applyPowersToCreature(monster, new UnawakenedPower(monster));
        applyPowersToCreature(monster, new AwakeningPower(monster));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new AwakeningMod();
    }
}
