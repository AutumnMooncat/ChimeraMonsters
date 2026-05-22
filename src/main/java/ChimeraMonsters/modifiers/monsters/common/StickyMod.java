package ChimeraMonsters.modifiers.monsters.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.StickyPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class StickyMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(StickyMod.class.getSimpleName());

    public StickyMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return true;
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseDamage(monster, DEBUFF_10);
        applyPowersToCreature(monster, new StickyPower(monster, Math.min(2, AbstractDungeon.actNum)));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new StickyMod();
    }
}
