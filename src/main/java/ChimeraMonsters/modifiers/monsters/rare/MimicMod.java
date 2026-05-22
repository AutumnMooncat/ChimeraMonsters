package ChimeraMonsters.modifiers.monsters.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.MimicPower;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.exordium.*;

public class MimicMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(MimicMod.class.getSimpleName());
    private AbstractMonster disguise;

    public MimicMod() {
        super(ID, ModifierRarity.RARE);
    }

    @Override
    public String modifyName(AbstractMonster monster) {
        return super.modifyName(disguise != null ? disguise : monster);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return true;
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        disguise = getDisguise();
        disguise.hb.move(monster.hb.cX, monster.hb.cY);
        applyPowersToCreature(monster, new MimicPower(monster, disguise, scaleAbilities(monster, 0.15f, 0.2f)));
    }

    public AbstractMonster getDisguise() {
        switch (AbstractDungeon.monsterRng.random(5)) {
            case 1:
                return new LouseNormal(0, 0);
            case 2:
                return new FungiBeast(0, 0);
            case 3:
                return new AcidSlime_M(0, 0);
            case 4:
                return new SpikeSlime_S(0, 0, 0);
            default:
                return new GremlinThief(0, 0);
        }
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new MimicMod();
    }
}
