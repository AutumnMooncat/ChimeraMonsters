package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.groups.themed.SimpleThemedModifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.SpikyPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.ThornsPower;

public class SpikyMod extends AbstractMonsterModifier implements SimpleThemedModifier {
    public static final String ID = ChimeraMonstersMod.makeID(SpikyMod.class.getSimpleName());

    public SpikyMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        if (getConstructedBaseHealth(monster) >= 100 || !noEliteOrBossCheck(monster, context) || !actAtLeast(2)) {
            return false;
        }
        return !hasAnyAnywhere(monster, PlatedArmorPower.class, BarricadePower.class, MetallicizePower.class, ThornsPower.class);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseHealth(monster, DEBUFF_25);
        int base = scaleDeadlier(monster, 3, 4);
        applyPowersToCreature(monster, new ThornsPower(monster, scaleAbilities(monster, base, base + 2)));
        applyPowersToCreature(monster, new SpikyPower(monster, 2));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new SpikyMod();
    }

    @Override
    public void themedPreBattle(MonsterGroup context) {
        int thorns = 3;
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ThornsPower(AbstractDungeon.player, thorns)));
    }
}
