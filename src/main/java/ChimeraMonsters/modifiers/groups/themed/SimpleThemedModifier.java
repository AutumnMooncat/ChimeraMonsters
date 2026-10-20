package ChimeraMonsters.modifiers.groups.themed;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;

public interface SimpleThemedModifier {
    default String fightName() {
        if (this instanceof AbstractMonsterModifier) {
            return ((AbstractMonsterModifier) this).getStrings().EXTRA_TEXT[0];
        }
        return null;
    }

    default void registerThemed() {
        if (this instanceof AbstractMonsterModifier) {
            AbstractMonsterModifier mod = (AbstractMonsterModifier) this;
            ChimeraMonstersMod.registerModifier(new AbstractThemedModifier(mod.identifier(), mod.makeCopy()) {
                @Override
                public String getModifierName() {
                    return fightName();
                }
            }, ChimeraMonstersMod.getModID());
        }
    }
}
