package ChimeraMonsters.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

public class TextureSniper {
    private static final ArrayList<Disposable> disposables = new ArrayList<>();

    public static Texture snipeCard(AbstractCard card) {
        AbstractCard toRender = card.makeStatEquivalentCopy();
        toRender.current_x = 0;
        toRender.current_y = 0;
        toRender.drawScale = 1.0f/Settings.scale;
        FrameBuffer fb = ImageHelper.createBuffer(AbstractCard.RAW_W+50, AbstractCard.RAW_H+50);
        SpriteBatch sb = new SpriteBatch();
        sb.setProjectionMatrix(new OrthographicCamera(AbstractCard.RAW_W+50, AbstractCard.RAW_H+50).combined);
        ImageHelper.beginBuffer(fb);
        sb.begin();
        toRender.render(sb);
        sb.end();
        fb.end();
        disposables.add(fb);
        disposables.add(sb);
        return flipRawTexture(ImageHelper.getBufferTexture(fb).getTexture());
    }

    public static Texture snipePower(AbstractPower p) {
        FrameBuffer fb = ImageHelper.createBuffer(50, 50);
        SpriteBatch sb = new SpriteBatch();
        sb.setProjectionMatrix(new OrthographicCamera(50, 50).combined);
        ImageHelper.beginBuffer(fb);
        sb.begin();
        p.renderIcons(sb, 0, 0, Color.WHITE.cpy());
        p.renderAmount(sb, 32, -18, Color.WHITE.cpy());
        sb.end();
        fb.end();
        disposables.add(fb);
        disposables.add(sb);
        return flipRawTexture(ImageHelper.getBufferTexture(fb).getTexture());
    }

    public static Texture snipePotion(AbstractPotion p) {
        FrameBuffer fb = ImageHelper.createBuffer(64, 64);
        SpriteBatch sb = new SpriteBatch();
        sb.setProjectionMatrix(new OrthographicCamera(64, 64).combined);
        ImageHelper.beginBuffer(fb);
        sb.begin();
        float x = p.posX;
        float y = p.posY;
        p.posX = 0;
        p.posY = 0;
        p.render(sb);
        p.posX = x;
        p.posY = y;
        sb.end();
        fb.end();
        disposables.add(fb);
        disposables.add(sb);
        return flipRawTexture(ImageHelper.getBufferTexture(fb).getTexture());
    }

    private static Texture flipRawTexture(Texture t) {
        //Rendering to fbo flips the texture, rendering it a second time flips it back
        int w = t.getWidth();
        int h = t.getHeight();
        float w2 = w/2f;
        float h2 = h/2f;
        FrameBuffer fb = ImageHelper.createBuffer(w, h);
        SpriteBatch sb = new SpriteBatch();
        sb.setProjectionMatrix(new OrthographicCamera(w, h).combined);
        ImageHelper.beginBuffer(fb);
        sb.begin();
        sb.draw(t, -w2, -h2, -w2, -h2, w, h, 1, 1, 0, 0, 0, w, h, false, false);
        sb.end();
        fb.end();
        t.dispose();
        Texture ret = ImageHelper.getBufferTexture(fb).getTexture();
        disposables.add(ret);
        disposables.add(fb);
        disposables.add(sb);
        return ret;
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "preBattlePrep")
    @SpirePatch2(clz = AbstractPlayer.class, method = "onVictory")
    public static class ClearDisposables {
        @SpirePostfixPatch
        public static void yeet() {
            for (Disposable d : disposables) {
                d.dispose();
            }
            disposables.clear();
        }
    }
}
