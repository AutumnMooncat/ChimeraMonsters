package ChimeraMonsters.util;

import basemod.BaseMod;
import basemod.abstracts.CustomReward;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardSave;

public interface RewardHelper {
    CustomReward getReward();
    RewardItem.RewardType getRewardType();

    default void registerReward() {
        BaseMod.registerCustomReward(getRewardType(), load -> getReward(), save -> new RewardSave(save.type.toString(), null));
    }
}
