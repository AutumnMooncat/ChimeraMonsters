package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.groups.themed.SimpleThemedModifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.powers.ExplodingPower;
import ChimeraMonsters.util.RewardHelper;
import basemod.abstracts.CustomReward;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.colorless.TheBomb;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.rewards.RewardItem;

public class ExplodingMod extends AbstractMonsterModifier implements SimpleThemedModifier, RewardHelper {
    public static final String ID = ChimeraMonstersMod.makeID(ExplodingMod.class.getSimpleName());

    public ExplodingMod() {
        super(ID, ModifierRarity.UNCOMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        if (getConstructedBaseHealth(monster) >= 30) {
            return false;
        }
        return !hasAnyAnywhere(monster, FlightPower.class);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        applyPowersToCreature(monster, new ExplodingPower(monster, 3, AbstractDungeon.actNum * 10));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new ExplodingMod();
    }

    @Override
    public void themedPreBattle(MonsterGroup context) {
        AbstractDungeon.getCurrRoom().rewards.add(getReward());
    }

    @Override
    public CustomReward getReward() {
        return new ExplodingModReward();
    }

    @Override
    public RewardItem.RewardType getRewardType() {
        return Enum.EXPLODING_MOD_REWARD;
    }

    public static class Enum {
        @SpireEnum
        public static RewardItem.RewardType EXPLODING_MOD_REWARD;
    }

    public static class ExplodingModReward extends CustomReward {
        public ExplodingModReward() {
            super(ImageMaster.REWARD_CARD_BOSS, RewardItem.TEXT[2], Enum.EXPLODING_MOD_REWARD);
            cards.add(new TheBomb());
        }
        @Override
        public boolean claimReward() {
            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD) {
                AbstractDungeon.cardRewardScreen.open(cards, this, RewardItem.TEXT[4]);
                AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.COMBAT_REWARD;
            }
            return false;
        }
    }
}
