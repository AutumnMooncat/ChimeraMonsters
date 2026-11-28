package ChimeraMonsters.util.compat;

import ChimeraMonsters.ChimeraMonstersMod;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.beyond.TimeEater;
import com.megacrit.cardcrawl.monsters.city.Champ;
import com.megacrit.cardcrawl.monsters.exordium.AcidSlime_L;
import com.megacrit.cardcrawl.monsters.exordium.SlimeBoss;
import com.megacrit.cardcrawl.monsters.exordium.SpikeSlime_L;

import java.lang.reflect.Method;

public class MintyCompat {
    public static boolean asplode;

    public static boolean mintyShowsHalfHealth(AbstractCreature c) {
        if (asplode) return false;

        if (Loader.isModLoaded("mintyspire")) {
            try {
                Method method = Class.forName("mintySpire.MintySpire").getMethod("showHH");
                return (boolean) method.invoke(null);
            } catch (Exception e) {
                if (!asplode) {
                    ChimeraMonstersMod.logger.error("Minty compat failed", e);
                    asplode = true;
                }
            }
        }
        return false;
    }

    public static boolean mintyAlreadyRenders(AbstractCreature c) {
        if (c.id == null) return false;
        return c.id.equals(SlimeBoss.ID) || c.id.equals(AcidSlime_L.ID) || c.id.equals(SpikeSlime_L.ID) || c.id.equals(Champ.ID) || c.id.equals(TimeEater.ID);
    }
}
