package ChimeraMonsters.modifiers.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.powers.HoverPower;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

public class KiteMod extends AbstractMonsterModifier {
    public static final String ID = ChimeraMonstersMod.makeID(KiteMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    public static final int ACT_ONE = 6;
    public static final int ACT_TWO_PLUS = 9;

    @Override
    public ModifierRarity getModRarity() {
        return ModifierRarity.UNCOMMON;
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        if (AbstractDungeon.getCurrRoom() == null) {
            return true;
        }
        return !(AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        int amount = 0;
        switch (AbstractDungeon.actNum) {
            case 1:
                amount = ACT_ONE;
                break;
            case 2:
            default:
                amount = ACT_TWO_PLUS;
        }
        applyPowersToCreature(monster, new HoverPower(monster, amount));
    }

    @Override
    public String identifier() {
        return ID;
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new KiteMod();
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
