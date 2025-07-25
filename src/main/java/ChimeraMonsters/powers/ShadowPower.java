package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.ShaderCompiler;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class ShadowPower extends AbstractInternalLogicPower implements RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(ShadowPower.class.getSimpleName());
    private static final ShaderProgram sp = ShaderCompiler.makeFrag(ChimeraMonstersMod.makePath("shaders/shadow.frag"));

    public ShadowPower(AbstractCreature owner, int amount) {
        super(POWER_ID, owner, amount);
    }

    @Override
    public ShaderProgram getShader() {
        return sp;
    }
}
