package ChimeraMonsters.modifiers.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.powers.MimicPower;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.exordium.*;

public class MimicMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(MimicMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private AbstractMonster disguise;

    @Override
    public ModifierRarity getModRarity() {
        return ModifierRarity.RARE;
    }

    @Override
    public String modifyName(AbstractMonster monster) {
        return super.modifyName(disguise != null ? disguise : monster);
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
    public String identifier() {
        return ID;
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new MimicMod();
    }
}
