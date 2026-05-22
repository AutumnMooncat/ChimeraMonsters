package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.HoverPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class KiteMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(KiteMod.class.getSimpleName());

    public KiteMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noBossCheck(monster, context);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        int amount = AbstractDungeon.actNum == 1 ? 3 : 6;
        applyPowersToCreature(monster, new HoverPower(monster, scaleAbilities(monster, amount, amount + 2)));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new KiteMod();
    }
}
