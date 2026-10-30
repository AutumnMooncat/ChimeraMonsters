package ChimeraMonsters.modifiers.groups.themed;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public interface SimpleThemedModifier {
    default String fightName() {
        if (this instanceof AbstractMonsterModifier) {
            try {
                return ((AbstractMonsterModifier) this).getStrings().EXTRA_TEXT[0];
            } catch (Exception e) {
                throw new RuntimeException("Chimera Monsters failed to get themed fight name for " + ((AbstractMonsterModifier) this).identifier());
            }
        }
        return null;
    }

    default void themedPreBattle(MonsterGroup context) {}

    default void registerThemed() {
        if (this instanceof AbstractMonsterModifier) {
            AbstractMonsterModifier mod = (AbstractMonsterModifier) this;
            ChimeraMonstersMod.registerModifier(new AbstractThemedModifier(mod.identifier(), mod.makeCopy()) {
                @Override
                public String getModifierName() {
                    return fightName();
                }

                @Override
                public void preBattleActions(MonsterGroup context) {
                    themedPreBattle(context);
                }
            }, ChimeraMonstersMod.getModID());
        }
    }
}
