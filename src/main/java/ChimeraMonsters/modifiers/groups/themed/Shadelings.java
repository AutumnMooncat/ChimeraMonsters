package ChimeraMonsters.modifiers.groups.themed;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.groups.AbstractMonsterGroupModifier;
import ChimeraMonsters.modifiers.monsters.uncommon.ShadowMod;

public class Shadelings extends AbstractThemedModifier {
    public static final String ID = ChimeraMonstersMod.makeID(Shadelings.class.getSimpleName());

    public Shadelings() {
        super(ID, new ShadowMod());
    }

    @Override
    public AbstractMonsterGroupModifier makeCopy() {
        return new Shadelings();
    }
}
