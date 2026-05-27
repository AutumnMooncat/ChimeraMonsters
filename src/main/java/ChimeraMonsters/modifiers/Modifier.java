package ChimeraMonsters.modifiers;

public interface Modifier<T> {
    enum ModifierRarity {
        COMMON,
        UNCOMMON,
        RARE,
        SPECIAL;

        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    ModifierRarity getModRarity();

    void applyTo(T target);

    String identifier();

    String getModifierName();

    String getModifierDescription();

    boolean canApplyTo(T target);

    Modifier<T> makeCopy();
}
