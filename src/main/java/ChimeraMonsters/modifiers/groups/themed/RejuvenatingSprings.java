package ChimeraMonsters.modifiers.groups.themed;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.groups.AbstractMonsterGroupModifier;
import ChimeraMonsters.modifiers.monsters.common.RegenerationMod;

public class RejuvenatingSprings extends AbstractThemedModifier {
    public static final String ID = ChimeraMonstersMod.makeID(RejuvenatingSprings.class.getSimpleName());

    public RejuvenatingSprings() {
        super(ID, new RegenerationMod());
    }

    @Override
    public AbstractMonsterGroupModifier makeCopy() {
        return new RejuvenatingSprings();
    }
}
