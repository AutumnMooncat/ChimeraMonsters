package ChimeraMonsters.util;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.helpers.TipHelper;

import java.util.function.Consumer;

public class TipBuffer {
    private static final FrameBuffer buffer = ImageHelper.createBuffer();
    private static boolean buffered;
    private static boolean toBuffer;

    public static void renderWith(Consumer<SpriteBatch> doRender) {
        if (!buffered) {
            ImageHelper.beginBuffer(buffer);
            buffered = true;
        } else {
            buffer.begin();
        }
        SpriteBatch sb = new SpriteBatch();
        sb.begin();
        sb.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        doRender.accept(sb);
        toBuffer = true;
        TipHelper.render(sb);
        toBuffer = false;
        sb.end();
        sb.dispose();
        buffer.end();
    }

    @SpirePatch2(clz = TipHelper.class, method = "render")
    public static class DoRender {
        @SpirePrefixPatch
        public static void plz(SpriteBatch sb) {
            if (toBuffer) {
                return;
            }
            if (buffered) {
                TextureRegion tex = ImageHelper.getBufferTexture(buffer);
                sb.draw(tex, 0, 0);
                buffered = false;
            }
        }
    }
}
