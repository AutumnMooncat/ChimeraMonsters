package ChimeraMonsters.util;

import basemod.BaseMod;
import basemod.Pair;
import basemod.interfaces.PostBattleSubscriber;
import basemod.interfaces.PostDeathSubscriber;
import basemod.interfaces.StartGameSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;

@SpireInitializer
public class LoopingSoundManager implements StartGameSubscriber, PostBattleSubscriber, PostDeathSubscriber {

    public LoopingSoundManager() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new LoopingSoundManager();
    }

    public static final ArrayList<Pair<String, Long>> loopedSounds = new ArrayList<>();

    public static Pair<String, Long> addLoopedSound(String key) {
        Pair<String, Long> p = new Pair<>(key, CardCrawlGame.sound.playAndLoop(key));
        loopedSounds.add(p);
        return p;
    }

    public static void stopLoopedSound(Pair<String, Long> pair) {
        CardCrawlGame.sound.stop(pair.getKey(), pair.getValue());
    }

    public static void stopAllLoopedSounds() {
        for (Pair<String, Long> p : loopedSounds) {
            stopLoopedSound(p);
        }
    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        stopAllLoopedSounds();
    }

    @Override
    public void receivePostDeath() {
        stopAllLoopedSounds();
    }

    @Override
    public void receiveStartGame() {
        stopAllLoopedSounds();
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "resetPlayer")
    @SpirePatch2(clz = CardCrawlGame.class, method = "startOver")
    public static class StopLoopingPlz {
        @SpirePostfixPatch
        public static void plz() {
            stopAllLoopedSounds();
        }
    }
}
