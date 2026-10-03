package ChimeraMonsters.util;

import basemod.eventUtil.AddEventParams;
import basemod.patches.com.megacrit.cardcrawl.events.AbstractEvent.AdditionalEventParameters;
import com.megacrit.cardcrawl.events.AbstractEvent;

public interface EventHelper {
    static void makeEndAtCombatRewards(AbstractEvent event) {
        AddEventParams params = AdditionalEventParameters.additionalParameters.get(event);
        if (params == null) {
            params = new AddEventParams();
        }
        params.endsWithRewardsUI = true;
        AdditionalEventParameters.additionalParameters.set(event, params);
    }

    static boolean screenIs(Object screen, String name) {
        if (screen instanceof Enum) {
            Enum<?> screenEnum = (Enum<?>) screen;
            return screenEnum.name().equals(name);
        }
        return false;
    }
}
