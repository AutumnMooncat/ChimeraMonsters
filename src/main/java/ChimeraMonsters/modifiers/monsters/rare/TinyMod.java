package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.TinyPower;
import ChimeraMonsters.util.FormatHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TinyMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(TinyMod.class.getSimpleName());

    public TinyMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noBossCheck(monster, context);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new TinyPower(monster));
        manipulateBaseDamage(monster, DEBUFF_33);
        manipulateBaseHealth(monster, DEBUFF_33);
    }

    @Override
    public String modifyName(AbstractMonster monster) {
        // Remove Giant instead of adding Tiny
        if (monster.name.contains(uiStrings.TEXT[3])) {
            String[] parts = monster.name.split(" ");
            for (int i = 0; i < parts.length; i++) {
                boolean wasCap = FormatHelper.isCapitalized(parts[i]);
                parts[i] = parts[i].replace(uiStrings.TEXT[3], "");
                if (wasCap && !FormatHelper.isCapitalized(parts[i])) {
                    parts[i] = FormatHelper.capitalize(parts[i]);
                }
            }
            return Arrays.stream(parts).filter(s -> !s.isEmpty()).collect(Collectors.joining());
        }
        return super.modifyName(monster);
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new TinyMod();
    }
}
