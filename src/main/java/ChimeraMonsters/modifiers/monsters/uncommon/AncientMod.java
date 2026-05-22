package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.AncientPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.ArtifactPower;

public class AncientMod extends AbstractMonsterModifier {
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
        applyPowersToCreature(monster, new ArtifactPower(monster, scaleAbilities(monster, 5, 6)));
        applyPowersToCreature(monster, new AncientPower(monster, 1));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new AncientMod();
    }
}
