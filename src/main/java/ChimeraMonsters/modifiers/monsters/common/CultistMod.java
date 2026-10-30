package ChimeraMonsters.modifiers.monsters.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.groups.themed.SimpleThemedModifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.CultistPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.RitualPower;
import com.megacrit.cardcrawl.relics.CultistMask;
import com.megacrit.cardcrawl.rewards.RewardItem;

public class CultistMod extends AbstractMonsterModifier implements SimpleThemedModifier {
    public static final String ID = ChimeraMonstersMod.makeID(CultistMod.class.getSimpleName());

    public CultistMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return /*noBossCheck(monster, context) &&*/ hasBone(monster, "head") && !hasAnyAnywhere(monster, RitualPower.class);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseDamage(monster, DEBUFF_25);
        int base = scaleDeadlier(monster, 2, 3);
        applyPowersToCreature(monster, new CultistPower(monster, scaleAbilities(monster, base, base + 1)));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new CultistMod();
    }

    @Override
    public void themedPreBattle(MonsterGroup context) {
        if (!AbstractDungeon.player.hasRelic(CultistMask.ID))
        {
            AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(new CultistMask()));
        }
    }
}
