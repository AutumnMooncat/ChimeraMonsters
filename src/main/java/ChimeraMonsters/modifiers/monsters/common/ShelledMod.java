package ChimeraMonsters.modifiers.monsters.common;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.patches.MonsterFields;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;

public class ShelledMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(ShelledMod.class.getSimpleName());

    public ShelledMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noEliteOrBossCheck(monster, context) && actAtLeast(2);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new PlatedArmorPower(monster, AbstractDungeon.actNum * 4));
        MonsterFields.stunOnArmorBreak.set(monster, true);
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new ShelledMod();
    }
}
