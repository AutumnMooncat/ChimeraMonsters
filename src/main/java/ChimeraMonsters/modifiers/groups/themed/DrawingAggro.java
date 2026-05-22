package ChimeraMonsters.modifiers.groups.themed;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.groups.AbstractMonsterGroupModifier;
import ChimeraMonsters.modifiers.monsters.common.AggressiveMod;

public class DrawingAggro extends AbstractThemedModifier {
    public static final String ID = ChimeraMonstersMod.makeID(DrawingAggro.class.getSimpleName());

    public DrawingAggro() {
        super(ID, new AggressiveMod());
    }

    @Override
    public AbstractMonsterGroupModifier makeCopy() {
        return new DrawingAggro();
    }
}
