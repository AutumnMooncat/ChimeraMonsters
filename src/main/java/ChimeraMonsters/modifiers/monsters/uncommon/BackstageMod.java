package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.BackstagePower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.beyond.OrbWalker;
import com.megacrit.cardcrawl.monsters.city.Taskmaster;
import com.megacrit.cardcrawl.monsters.exordium.Cultist;

public class BackstageMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(BackstageMod.class.getSimpleName());

    public BackstageMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        // TODO check for enemy scaling instead of hardcoding
        if (context != null && context.monsters != null && context.monsters.size() <= 1) {
            return false;
        } else if (monster.id.equals(Taskmaster.ID) || monster.id.equals(Cultist.ID) || monster.id.equals(OrbWalker.ID)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new BackstagePower(monster, 1));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new BackstageMod();
    }
}
