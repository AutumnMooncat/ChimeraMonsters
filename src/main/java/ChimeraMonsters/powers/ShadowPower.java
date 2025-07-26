package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.ShaderCompiler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class ShadowPower extends AbstractInternalLogicPower implements RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(ShadowPower.class.getSimpleName());
    private static final ShaderProgram sp = ShaderCompiler.makeFrag(ChimeraMonstersMod.makePath("shaders/shadow.frag"));
    private static final ShaderProgram sp2 = ShaderCompiler.makeFrag(ChimeraMonstersMod.makePath("shaders/aberration.frag"));
    private static final ShaderProgram sp3 = ShaderCompiler.makeFrag(ChimeraMonstersMod.makePath("shaders/sobel.frag"));
    private static final Color TEST_COLOR = new Color(0.8f, 0.8f, 0.8f, 0.8f);

    public ShadowPower(AbstractCreature owner, int amount) {
        super(POWER_ID, owner, amount);
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        Color origColor = sb.getColor();
        sb.setColor(Color.WHITE);
        render(sb, tex, 100, 50, 1.2f, 10);
        blitShader(sb, tex, sp);
        render(sb, tex);
        sb.setColor(origColor);
    }
}
