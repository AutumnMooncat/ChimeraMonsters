package ChimeraMonsters.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class TextureScaler {
    public static Texture rescale(Texture t, float scale) {
        return rescale(t, (int) (t.getWidth() * scale), (int) (t.getHeight() * scale));
    }

    public static Texture rescale(Texture t, int w, int h) {
        float w2 = w/2f;
        float h2 = h/2f;
        FrameBuffer fb = ImageHelper.createBuffer(w, h);
        SpriteBatch sb = new SpriteBatch();
        sb.setProjectionMatrix(new OrthographicCamera(w, h).combined);
        ImageHelper.beginBuffer(fb);
        sb.begin();
        sb.draw(t, -w2, -h2, -w2, -h2, w, h, 1, 1, 0, 0, 0, t.getWidth(), t.getHeight(), false, true);
        sb.end();
        fb.end();
        return ImageHelper.getBufferTexture(fb).getTexture();
    }

    public static Texture rescale(TextureAtlas.AtlasRegion r, float scale) {
        return rescale(r, (int) (r.packedWidth * scale), (int) (r.packedHeight * scale));
    }

    public static Texture rescale(TextureAtlas.AtlasRegion r, int w, int h) {
        float w2 = w/2f;
        float h2 = h/2f;
        FrameBuffer fb = ImageHelper.createBuffer(w, h);
        SpriteBatch sb = new SpriteBatch();
        sb.setProjectionMatrix(new OrthographicCamera(w, h).combined);
        ImageHelper.beginBuffer(fb);
        r.flip(false, true);
        sb.begin();
        sb.draw(r, -w2, -h2, -w2, -h2, w, h, 1, 1, 0);
        sb.end();
        fb.end();
        r.flip(false, true);
        return ImageHelper.getBufferTexture(fb).getTexture();
    }
}
