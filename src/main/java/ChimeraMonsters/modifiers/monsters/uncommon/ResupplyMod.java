package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.actions.TimedAction;
import ChimeraMonsters.actions.TimedVFXAction;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.DelayedMonsterPower;
import ChimeraMonsters.powers.InterceptionPower;
import ChimeraMonsters.powers.ResupplyPower;
import ChimeraMonsters.util.Wiz;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class ResupplyMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(ResupplyMod.class.getSimpleName());

    public ResupplyMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return checkContext(context, multiCombat) && checkContext(context, monster, lastMonster) && noBossCheck(monster, context);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        int block = scaleAbilities(monster, 5, 6) * (AbstractDungeon.actNum + 1);
        int str = scaleDeadlier(monster, AbstractDungeon.actNum + 1, AbstractDungeon.actNum + 2);
        str = scaleAbilities(monster, str, str + 1);
        applyPowersToCreature(monster, new ResupplyPower(monster, block, str));
    }

    @Override
    public void preBattleActions(AbstractMonster monster, MonsterGroup context) {
        addToBot(new TimedAction(t -> {
            if (t == 0f) {
                monster.tint.changeColor(new Color(1f, 1f, 1f, 0f), 3f);
            } else if (t >= 1f) {
                context.monsters.remove(monster);
                applyPowersToCreature(AbstractDungeon.player, new DelayedMonsterPower(AbstractDungeon.player, monster, 1));
                return true;
            }
            return false;
        }));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new ResupplyMod();
    }
}
