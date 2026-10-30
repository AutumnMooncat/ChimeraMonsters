package ChimeraMonsters.modifiers.monsters.uncommon;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.groups.themed.SimpleThemedModifier;
import ChimeraMonsters.modifiers.monsters.AbstractMonsterModifier;
import ChimeraMonsters.util.RewardHelper;
import basemod.abstracts.CustomReward;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.tempCards.Shiv;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.PainfulStabsPower;
import com.megacrit.cardcrawl.rewards.RewardItem;

public class StabbingMod extends AbstractMonsterModifier implements SimpleThemedModifier, RewardHelper {
    public static final String ID = ChimeraMonstersMod.makeID(StabbingMod.class.getSimpleName());

    public StabbingMod() {
        super(ID, ModifierRarity.COMMON);
    }

    @Override
    protected boolean validMonster(AbstractMonster monster, MonsterGroup context) {
        return noBossCheck(monster, context) && actAtLeast(2) && !hasAnyAnywhere(monster, PainfulStabsPower.class);
    }

    @Override
    public void applyTo(AbstractMonster monster) {
        manipulateBaseDamage(monster, DEBUFF_10);
        applyPowersToCreature(monster, new PainfulStabsPower(monster));
    }

    @Override
    public AbstractMonsterModifier makeCopy() {
        return new StabbingMod();
    }

    @Override
    public void themedPreBattle(MonsterGroup context) {
        AbstractDungeon.getCurrRoom().rewards.add(getReward());
    }

    @Override
    public CustomReward getReward() {
        return new StabbingModReward();
    }

    @Override
    public RewardItem.RewardType getRewardType() {
        return Enum.STABBING_MOD_REWARD;
    }

    public static class Enum {
        @SpireEnum
        public static RewardItem.RewardType STABBING_MOD_REWARD;
    }

    public static class StabbingModReward extends CustomReward {
        public StabbingModReward() {
            super(ImageMaster.REWARD_CARD_NORMAL, RewardItem.TEXT[2], Enum.STABBING_MOD_REWARD);
            cards.add(new Shiv());
            cards.add(new Shiv());
            cards.add(new Shiv());
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
