package ChimeraMonsters.modifiers.groups.themed;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public interface SimpleThemedModifier {
    default String fightName() {
        if (this instanceof AbstractMonsterModifier) {
            return ((AbstractMonsterModifier) this).getStrings().EXTRA_TEXT[0];
        }
        return null;
    }

    default void onPreBattle(MonsterGroup context) {}

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
                    onPreBattle(context);
                }
            }, ChimeraMonstersMod.getModID());
        }
    }
}
