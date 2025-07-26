package ChimeraMonsters.util;

import ChimeraMonsters.ChimeraMonstersMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import org.apache.logging.log4j.Level;

import java.nio.charset.StandardCharsets;

public class ShaderCompiler {
    private static final ShaderProgram defaultShader = SpriteBatch.createDefaultShader();

    public static ShaderProgram makeFrag(String frag) {
        return make(null, frag);
    }

    public static ShaderProgram makeVert(String vert) {
        return make(vert, null);
    }

    public static ShaderProgram make(String vert, String frag) {
        ShaderProgram prog = new ShaderProgram(
                vert == null ? defaultShader.getVertexShaderSource() : Gdx.files.internal(vert).readString(String.valueOf(StandardCharsets.UTF_8)),
                frag == null ? defaultShader.getFragmentShaderSource() : Gdx.files.internal(frag).readString(String.valueOf(StandardCharsets.UTF_8))
        );
        if (prog.isCompiled()) {
            ChimeraMonstersMod.logger.log(Level.INFO, "Shader compiled");
            ChimeraMonstersMod.logger.log(Level.INFO, "Vert: {}, Frag: {}", vert == null ? "default" : vert, frag == null ? "default" : frag);
            return prog;
        }
        ChimeraMonstersMod.logger.log(Level.ERROR, "Shader failed to compile");
        ChimeraMonstersMod.logger.log(Level.ERROR, "Vert: {}, Frag: {}", vert == null ? "default" : vert, frag == null ? "default" : frag);
        ChimeraMonstersMod.logger.log(Level.ERROR, "Log: {}", prog.getLog());
        return null;
    }
}
