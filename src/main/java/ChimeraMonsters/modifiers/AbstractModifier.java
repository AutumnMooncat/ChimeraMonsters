package ChimeraMonsters.modifiers;

public abstract class AbstractModifier<T> {
    public enum ModifierRarity {
        COMMON,
        UNCOMMON,
        RARE,
        SPECIAL;

        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    public abstract ModifierRarity getModRarity();

    public abstract void applyTo(T target);

    public abstract String identifier();

    public abstract String getModifierName();

    public abstract String getModifierDescription();

    public abstract boolean canApplyTo(T target);

    public abstract AbstractModifier<T> makeCopy();
}
