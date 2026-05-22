package ChimeraMonsters.modifiers.groups.themed;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.groups.AbstractMonsterGroupModifier;
import ChimeraMonsters.modifiers.monsters.common.VampiricMod;

public class Bloodlust extends AbstractThemedModifier {
    public static final String ID = ChimeraMonstersMod.makeID(Bloodlust.class.getSimpleName());

    public Bloodlust() {
        super(ID, new VampiricMod());
    }

    @Override
    public AbstractMonsterGroupModifier makeCopy() {
        return new Bloodlust();
    }
}
