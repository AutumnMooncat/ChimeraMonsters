package ChimeraMonsters.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EmptyRoom;

public class RoomSafetyPatches {
    public static AbstractRoom dummyRoom;
    public static AbstractPlayer dummyPlayer;

    @SpirePatch2(clz = AbstractDungeon.class, method = "getCurrRoom")
    public static class NoExplodeRoom {
        @SpirePrefixPatch
        public static SpireReturn<?> plz() {
            if (AbstractDungeon.currMapNode == null) {
                if (dummyRoom == null) {
                    dummyRoom = new EmptyRoom();
                    dummyRoom.monsters = new MonsterGroup(new AbstractMonster[]{});
                }
                return SpireReturn.Return(dummyRoom);
            }
            return SpireReturn.Continue();
        }
    }

    public static AbstractPlayer getDummyPlayer() {
        if (dummyPlayer == null) {
            dummyPlayer =  CardCrawlGame.characterManager.recreateCharacter(AbstractPlayer.PlayerClass.IRONCLAD);
        }
        return dummyPlayer;
    }
}
