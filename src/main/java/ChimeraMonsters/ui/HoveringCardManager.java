package ChimeraMonsters.ui;

import ChimeraMonsters.patches.MonsterModifierFieldPatches;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.BobEffect;

public class HoveringCardManager {
    private static final float Y_OFFSET = 170f * Settings.scale; // 170
    private static final float X_OFFSET = 100f * Settings.scale; // 100
    private final BobEffect bob = new BobEffect(3.0f * Settings.scale, 3.0f);
    public final CardGroup cards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    public AbstractCard hovered;
    public AbstractCreature owner;

    public HoveringCardManager(AbstractCreature owner) {
        this.owner = owner;
    }

    public void render(SpriteBatch sb) {
        for (AbstractCard card : cards.group) {
            if (card != hovered) {
                card.render(sb);
            }
        }
        if (hovered != null) {
            hovered.render(sb);
            TipHelper.renderTipForCard(hovered, sb, hovered.keywords);
        }
    }

    public void update() {
        bob.update();
        int i = 0;
        int j = 0;
        hovered = null;
        for (AbstractCard card : cards.group) {
            card.target_y = owner.hb.cY + owner.hb.height/2f + Y_OFFSET*(j+1) + bob.y;
            card.target_x = owner.hb.cX - X_OFFSET * Math.min(9, (cards.size()-1-10*j)) / 2f + X_OFFSET * i;
            card.targetAngle = 0f;
            card.update();
            card.hb.update();
            if (card.hb.hovered && hovered == null) {
                card.targetDrawScale = 0.75f;
                hovered = card;
            } else {
                card.targetDrawScale = 0.3f; // 0.2
            }
            card.applyPowers();
            i++;
            if (i == 10) {
                i = 0;
                j++;
            }
        }
    }


    public void addCard(AbstractCard card) {
        addCard(card, true, false);
    }

    public void addCard(AbstractCard card, boolean playSFX) {
        addCard(card, playSFX, false);
    }

    public void addCard(AbstractCard card, boolean playSFX, boolean isEndTurn) {
        card.targetAngle = 0f;
        card.beginGlowing();
        cards.addToTop(card);
        if (card instanceof OnStashedCard) {
            ((OnStashedCard) card).onStash();
        }
        for (AbstractPower p : owner.powers) {
            if (p instanceof OnStashPower) {
                ((OnStashPower) p).onStash(card, isEndTurn);
            }
        }
        if (playSFX) {
            CardCrawlGame.sound.play("CARD_REJECT", 0.1F); // CARD_SELECT CARD_REJECT CARD_OBTAIN
        }
    }

    public interface OnStashedCard {
        void onStash();
    }

    public interface OnStashPower {
        void onStash (AbstractCard card, boolean isEndTurn);
    }

    @SpirePatch2(clz = AbstractMonster.class, method = "die", paramtypez = {boolean.class})
    public static class EmptyCards {
        @SpirePostfixPatch
        public static void yeet(AbstractMonster __instance) {
            HoveringCardManager manager = MonsterModifierFieldPatches.ModifierFields.cardManager.get(__instance);
            if (manager != null) {
                manager.cards.clear();
            }
        }
    }
}
