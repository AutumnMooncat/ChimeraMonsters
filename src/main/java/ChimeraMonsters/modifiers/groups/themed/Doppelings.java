package ChimeraMonsters.modifiers.groups.themed;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.groups.AbstractMonsterGroupModifier;
import ChimeraMonsters.modifiers.monsters.rare.DoppelMod;

public class Doppelings extends AbstractThemedModifier {
    public static final String ID = ChimeraMonstersMod.makeID(Doppelings.class.getSimpleName());

    public Doppelings() {
        super(ID, new DoppelMod());
    }

    @Override
    public AbstractMonsterGroupModifier makeCopy() {
        return new Doppelings();
    }
}
