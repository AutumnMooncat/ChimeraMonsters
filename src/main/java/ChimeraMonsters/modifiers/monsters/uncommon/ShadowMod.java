package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.groups.themed.SimpleThemedModifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.orbs.MonsterDark;
import ChimeraMonsters.powers.ShadowPower;
import ChimeraMonsters.util.MonsterOrbHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class ShadowMod extends AbstractMonsterModifier implements SimpleThemedModifier {
    public static final String ID = ChimeraMonstersMod.makeID(ShadowMod.class.getSimpleName());

    public ShadowMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return true;
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseDamage(monster, DEBUFF_33);
        applyPowersToCreature(monster, new ShadowPower(monster));
    }

    @Override
    public void preBattleActions(AbstractMonster monster, MonsterGroup context) {
        addToBot(new MonsterOrbHelper.MonsterChannelAction(monster, new MonsterDark(), false));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new ShadowMod();
    }
}
