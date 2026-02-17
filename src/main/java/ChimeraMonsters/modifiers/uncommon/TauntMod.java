package ChimeraMonsters.modifiers.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.powers.TauntPower;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.beyond.OrbWalker;
import com.megacrit.cardcrawl.monsters.city.Taskmaster;
import com.megacrit.cardcrawl.monsters.exordium.Cultist;
import com.megacrit.cardcrawl.powers.MinionPower;

import java.util.List;

public class TauntMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(TauntMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public ModifierRarity getModRarity() {
        return ModifierRarity.UNCOMMON;
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        if (context == null || context.monsters == null) {
            return true;
        }
        if (context.monsters.size() <= 1 || monster.hasPower(MinionPower.POWER_ID)) {
            return false;
        }
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
    public String identifier() {
        return ID;
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new TauntMod();
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSuffix() {
        return TEXT[1];
    }

    @Override
    public String getModifierDescription() {
        return TEXT[2];
    }
}
