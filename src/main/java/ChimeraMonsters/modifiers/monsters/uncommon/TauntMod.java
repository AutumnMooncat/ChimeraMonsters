package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.TauntPower;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.beyond.OrbWalker;
import com.megacrit.cardcrawl.monsters.city.Taskmaster;
import com.megacrit.cardcrawl.monsters.exordium.Cultist;
import com.megacrit.cardcrawl.powers.MinionPower;

public class TauntMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(TauntMod.class.getSimpleName());

    public TauntMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        if (!checkContext(context, multiCombat)) {
            return false;
        }
        if (hasAnyAnywhere(monster, MinionPower.class)) {
            return false;
        }
        // TODO check for enemy scaling instead of hardcoding
        for (AbstractMonster m : context.monsters) {
            if (monster.equals(m)) {
                continue;
            }
            if (m.id.equals(Taskmaster.ID) || m.id.equals(Cultist.ID) || m.id.equals(OrbWalker.ID)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new TauntPower(monster, 1));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new TauntMod();
    }
}
