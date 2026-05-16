package ChimeraMonsters.modifiers.rare;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.powers.AwakeningPower;
import ChimeraMonsters.powers.HexaghostPower;
import ChimeraMonsters.util.matchers.SuperFieldAccessMatcher;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.UnawakenedPower;

public class HexaghostMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(HexaghostMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public ModifierRarity getModRarity() {
        return ModifierRarity.RARE;
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
        return monster.type != AbstractMonster.EnemyType.BOSS && actAtLeast(2);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseHealth(monster, DEBUFF_20);
        int base = AbstractDungeon.actNum + (monster.type == AbstractMonster.EnemyType.ELITE ? 1 : 0);
        applyPowersToCreature(monster, new HexaghostPower(monster, scaleDeadlier(monster, base, base + 1)));
    }

    @Override
    public String identifier() {
        return ID;
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new HexaghostMod();
    }
}
