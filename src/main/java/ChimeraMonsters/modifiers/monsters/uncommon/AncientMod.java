package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.groups.themed.SimpleThemedModifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.AncientPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.ArtifactPower;

public class AncientMod extends AbstractMonsterModifier implements SimpleThemedModifier {
    public static final String ID = ChimeraMonstersMod.makeID(AncientMod.class.getSimpleName());

    public AncientMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return true;
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new AncientPower(monster, 1));
    }

    @Override
    public void preBattleActions(AbstractMonster monster, MonsterGroup context) {
        addToBot(new ApplyPowerAction(monster, monster, new ArtifactPower(monster, scaleAbilities(monster, 5, 6))));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new AncientMod();
    }
}
