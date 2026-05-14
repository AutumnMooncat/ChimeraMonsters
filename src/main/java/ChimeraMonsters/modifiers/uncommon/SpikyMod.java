package ChimeraMonsters.modifiers.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.powers.SpikyPower;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.BarricadePower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

public class SpikyMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(SpikyMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public ModifierRarity getModRarity() {
        return ModifierRarity.COMMON;
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        if (getConstructedBaseHealth(monster) >= 100 || !noEliteOrBossCheck(monster, context) || !actAtLeast(2)) {
            return false;
        }
        return !hasAnyAnywhere(monster, PlatedArmorPower.class, BarricadePower.class, MetallicizePower.class, ThornsPower.class);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseHealth(monster, DEBUFF_25);
        int base = scaleDeadlier(monster, 3, 4);
        applyPowersToCreature(monster, new ThornsPower(monster, scaleAbilities(monster, base, base + 2)));
        applyPowersToCreature(monster, new SpikyPower(monster, 2));
    }

    @Override
    public String identifier() {
        return ID;
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new SpikyMod();
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
