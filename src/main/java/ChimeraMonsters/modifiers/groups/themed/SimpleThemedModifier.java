package ChimeraMonsters.modifiers.groups.themed;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import basemod.AutoAdd;
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
            ChimeraMonstersMod.registerModifier(new IntersectionThemedModifier((AbstractMonsterModifier & SimpleThemedModifier) this), ChimeraMonstersMod.getModID());
        }
    }

    @AutoAdd.Ignore
    class IntersectionThemedModifier extends AbstractThemedModifier {
        public <T extends AbstractMonsterModifier & SimpleThemedModifier> IntersectionThemedModifier(T mod) {
            super(mod.identifier(), mod);
        }

        @Override
        public String identifier() {
            return monsterMod.identifier()+"Themed";
        }

        @Override
        public void preBattleActions(MonsterGroup context) {
            ((SimpleThemedModifier) monsterMod).themedPreBattle(context);
        }

        @Override
        public String getModifierName() {
            return ((SimpleThemedModifier) monsterMod).fightName();
        }

        @Override
        public IntersectionThemedModifier makeCopy() {
            return new IntersectionThemedModifier((AbstractMonsterModifier & SimpleThemedModifier) monsterMod);
        }
    }
}
