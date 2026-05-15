package ChimeraMonsters.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class ColorUtil {
    public static final MutableColor RED = new MutableColor(0xFF0000FF);
    public static final MutableColor SCARLET = new MutableColor(0xFF2000FF);
    public static final MutableColor VERMILION = new MutableColor(0xFF4000FF);
    public static final MutableColor PERSIMMON = new MutableColor(0xFF6000FF);
    public static final MutableColor ORANGE = new MutableColor(0xFF8000FF);
    public static final MutableColor ORANGE_PEEL = new MutableColor(0xFFA000FF);
    public static final MutableColor AMBER = new MutableColor(0xFFC000FF);
    public static final MutableColor GOLDEN_YELLOW = new MutableColor(0xFFE000FF);
    public static final MutableColor YELLOW = new MutableColor(0xFFFF00FF);
    public static final MutableColor LEMON = new MutableColor(0xE0FF00FF);
    public static final MutableColor LIME = new MutableColor(0xC0FF00FF);
    public static final MutableColor SPRING_BUG = new MutableColor(0xA0FF00FF);
    public static final MutableColor CHARTREUSE = new MutableColor(0x80FF00FF);
    public static final MutableColor BRIGHT_GREEN = new MutableColor(0x60FF00FF);
    public static final MutableColor HARLEQUIN = new MutableColor(0x40FF00FF);
    public static final MutableColor NEON_GREEN = new MutableColor(0x20FF00FF);
    public static final MutableColor GREEN =  new MutableColor(0x00FF00FF);
    public static final MutableColor JADE = new MutableColor(0x00FF20FF);
    public static final MutableColor ERIN = new MutableColor(0x00FF40FF);
    public static final MutableColor EMERALD = new MutableColor(0x00FF60FF);
    public static final MutableColor SPRING_GREEN = new MutableColor(0x00FF80FF);
    public static final MutableColor MINT = new MutableColor(0x00FFA0FF);
    public static final MutableColor AQUAMARINE = new MutableColor(0x00FFC0FF);
    public static final MutableColor TURQUOISE = new MutableColor(0x00FFE0FF);
    public static final MutableColor CYAN =  new MutableColor(0x00FFFFFF);
    public static final MutableColor SKY_BLUE = new MutableColor(0x00E0FFFF);
    public static final MutableColor CAPRI = new MutableColor(0x00C0FFFF);
    public static final MutableColor CORNFLOWER = new MutableColor(0x00A0FFFF);
    public static final MutableColor AZURE = new MutableColor(0x0080FFFF);
    public static final MutableColor COBALT = new MutableColor(0x0060FFFF);
    public static final MutableColor CERULEAN = new MutableColor(0x40FFFF);
    public static final MutableColor SAPPHIRE = new MutableColor(0x0020FFFF);
    public static final MutableColor BLUE = new MutableColor(0x0000FFFF);
    public static final MutableColor IRIS = new MutableColor(0x2000FFFF);
    public static final MutableColor INDIGO = new MutableColor(0x4000FFFF);
    public static final MutableColor VERONICA = new MutableColor(0x6000FFFF);
    public static final MutableColor VIOLET = new MutableColor(0x8000FFFF);
    public static final MutableColor AMETHYST = new MutableColor(0xA000FFFF);
    public static final MutableColor PURPLE = new MutableColor(0xC000FFFF);
    public static final MutableColor PHLOX = new MutableColor(0xE000FFFF);
    public static final MutableColor MAGENTA = new MutableColor(0xFF00FFFF);
    public static final MutableColor FUCHSIA = new MutableColor(0xFF00E0FF);
    public static final MutableColor CERISE = new MutableColor(0xFF00C0FF);
    public static final MutableColor DEEP_PINK = new MutableColor(0xFF00A0FF);
    public static final MutableColor ROSE = new MutableColor(0xFF0080FF);
    public static final MutableColor RASPBERRY = new MutableColor(0xFF0060FF);
    public static final MutableColor CRIMSON = new MutableColor(0xFF0040FF);
    public static final MutableColor AMARANTH = new MutableColor(0xFF0020FF);

    public static final MutableColor PLATINUM = new MutableColor(0xE5E4E2FF);
    public static final MutableColor GOLD = new MutableColor(0xFFD700FF);
    public static final MutableColor SILVER = new MutableColor(0xC0C0C0FF);
    public static final MutableColor BRONZE = new MutableColor(0xCD7F32FF);

    public static final MutableColor WHITE = new MutableColor(1, 1, 1, 1);
    public static final MutableColor LIGHT_GRAY =  new MutableColor(0.75f, 0.75f, 0.75f, 1);
    public static final MutableColor GRAY =  new MutableColor(0.5f, 0.5f, 0.5f, 1);
    public static final MutableColor DARK_GRAY =  new MutableColor(0.25f, 0.25f, 0.25f, 1);
    public static final MutableColor BLACK = new MutableColor(0, 0, 0, 1);

    public static final MutableColor TRANSPARENT = new MutableColor(0, 0, 0, 0);

    public static MutableColor getRainbowColor() {
        return new MutableColor(
                (MathUtils.cosDeg((float)(System.currentTimeMillis() / 10L % 360L)) + 1.25F) / 2.3F,
                (MathUtils.cosDeg((float)((System.currentTimeMillis() + 1000L) / 10L % 360L)) + 1.25F) / 2.3F,
                (MathUtils.cosDeg((float)((System.currentTimeMillis() + 2000L) / 10L % 360L)) + 1.25F) / 2.3F,
                1.0f);
    }

    public static class MutableColor extends Color {
        public MutableColor() {
            super();
        }

        public MutableColor(int rgba8888) {
            super(rgba8888);
        }

        public MutableColor(float r, float g, float b, float a) {
            super(r, g, b, a);
        }

        public MutableColor(Color color) {
            super(color);
        }

        @Override
        public MutableColor cpy() {
            return new MutableColor(this);
        }

        @Override
        public MutableColor set(Color color) {
            return (MutableColor) super.set(color);
        }

        @Override
        public MutableColor mul(Color color) {
            return (MutableColor) super.mul(color);
        }

        @Override
        public MutableColor mul(float value) {
            return (MutableColor) super.mul(value);
        }

        @Override
        public MutableColor add(Color color) {
            return (MutableColor) super.add(color);
        }

        @Override
        public MutableColor sub(Color color) {
            return (MutableColor) super.sub(color);
        }

        @Override
        public MutableColor clamp() {
            return (MutableColor) super.clamp();
        }

        @Override
        public MutableColor set(float r, float g, float b, float a) {
            return (MutableColor) super.set(r, g, b, a);
        }

        @Override
        public MutableColor set(int rgba) {
            return (MutableColor) super.set(rgba);
        }

        @Override
        public MutableColor add(float r, float g, float b, float a) {
            return (MutableColor) super.add(r, g, b, a);
        }

        @Override
        public MutableColor sub(float r, float g, float b, float a) {
            return (MutableColor) super.sub(r, g, b, a);
        }

        @Override
        public MutableColor mul(float r, float g, float b, float a) {
            return (MutableColor) super.mul(r, g, b, a);
        }

        @Override
        public MutableColor lerp(Color target, float t) {
            return (MutableColor) super.lerp(target, t);
        }

        @Override
        public MutableColor lerp(float r, float g, float b, float a, float t) {
            return (MutableColor) super.lerp(r, g, b, a, t);
        }

        @Override
        public MutableColor premultiplyAlpha() {
            return (MutableColor) super.premultiplyAlpha();
        }

        public MutableColor diffuse(float var) {
            return diffuse(var, var, var, 0);
        }

        public MutableColor diffuse(float rVar, float gVar, float bVar) {
            return diffuse(rVar, gVar, bVar, 0);
        }

        public MutableColor diffuse(float rVar, float gVar, float bVar, float aVar) {
            return new MutableColor(r + MathUtils.random(-rVar, rVar), g + MathUtils.random(-gVar, gVar), b + MathUtils.random(-bVar, bVar), a + MathUtils.random(-aVar, aVar));
        }

        public MutableColor mix(Color c) {
            return cpy().lerp(c, 0.5f);
        }

        public MutableColor lighten() {
            return cpy().lerp(Color.WHITE, 0.25f);
        }

        public MutableColor darken() {
            return cpy().lerp(Color.BLACK, 0.25f);
        }

        public MutableColor pastel() {
            return colorFromHSL(getHue(this), getSat(this), 0.8f, this.a);
        }

        public MutableColor resaturate(Color c, float sat) {
            return colorFromHSL(getHue(c), Math.min(Math.max(sat, 0), 1), getLight(c), c.a);
        }
    }

    public static MutableColor diffuse(Color c, float var) {
        return diffuse(c, var, var, var, 0);
    }

    public static MutableColor diffuse(Color c, float rVar, float gVar, float bVar) {
        return diffuse(c, rVar, gVar, bVar, 0);
    }

    public static MutableColor diffuse(Color c, float rVar, float gVar, float bVar, float aVar) {
        return new MutableColor(c.r + MathUtils.random(-rVar, rVar), c.g + MathUtils.random(-gVar, gVar), c.b + MathUtils.random(-bVar, bVar), c.a + MathUtils.random(-aVar, aVar));
    }

    public static MutableColor mix(Color c1, Color c2) {
        return new MutableColor(c1).lerp(c2, 0.5f);
    }

    public static MutableColor lighten(Color c) {
        return new MutableColor(c).lerp(Color.WHITE, 0.25f);
    }

    public static MutableColor darken(Color c) {
        return new MutableColor(c).lerp(Color.BLACK, 0.25f);
    }

    public static MutableColor pastel(Color c) {
        return colorFromHSL(getHue(c), getSat(c), 0.8f, c.a);
    }

    public static MutableColor resaturate(Color c, float sat) {
        return colorFromHSL(getHue(c), Math.min(Math.max(sat, 0), 1), getLight(c), c.a);
    }

    public static MutableColor colorFromHSL(float hue, float sat, float light, float alpha) {
        float d = sat * (1 - Math.abs(2*light - 1));
        float x = d * (1 - Math.abs(((hue/60f)%2) - 1));
        float m = light - d/2f;
        if (0 <= hue && hue < 60) {
            return new MutableColor(d + m, x + m, m, alpha);
        } else if (60 <= hue && hue < 120) {
            return new MutableColor(x + m, d + m, m, alpha);
        } else if (120 <= hue && hue < 180) {
            return new MutableColor(m, d + m, x + m, alpha);
        } else if (180 <= hue && hue < 240) {
            return new MutableColor(m, x + m, d + m, alpha);
        } else if (240 <= hue && hue < 300) {
            return new MutableColor(x + m, m, d + m, alpha);
        } else {
            return new MutableColor(d + m, m, x + m, alpha);
        }
    }

    private static float getHue(Color c) {
        float max = c.r;
        float min = c.r;
        if (c.g > max) {
            max = c.g;
        }
        if (c.b > max) {
            max = c.b;
        }
        if (c.g < min) {
            min = c.g;
        }
        if (c.b < min) {
            min = c.b;
        }
        float delta = max - min;
        if (delta == 0) {
            return 0;
        }
        if (c.g >= c.b) {
            return (float) Math.toDegrees(Math.acos((c.r - c.g/2 - c.b/2)/Math.sqrt(c.r*c.r + c.g*c.g + c.b*c.b - c.r*c.g - c.r*c.b - c.g*c.b)));
        } else {
            return 360 - (float) Math.toDegrees(Math.acos((c.r - c.g/2 - c.b/2)/Math.sqrt(c.r*c.r + c.g*c.g + c.b*c.b - c.r*c.g - c.r*c.b - c.g*c.b)));
        }
    }

    private static float getSat(Color c) {
        float max = c.r;
        float min = c.r;
        if (c.g > max) {
            max = c.g;
        }
        if (c.b > max) {
            max = c.b;
        }
        if (c.g < min) {
            min = c.g;
        }
        if (c.b < min) {
            min = c.b;
        }
        float delta = max - min;
        if (delta == 0) {
            return 0;
        }
        float lightness = (max + min)/2f;
        return delta / (1 - Math.abs(2*lightness - 1));
    }

    private static float getLight(Color c) {
        float max = c.r;
        float min = c.r;
        if (c.g > max) {
            max = c.g;
        }
        if (c.b > max) {
            max = c.b;
        }
        if (c.g < min) {
            min = c.g;
        }
        if (c.b < min) {
            min = c.b;
        }
        return (max + min)/2f;
    }

    private static float getLum(Color c) {
        return 0.2126f * c.r + 0.7152f * c.g + 0.0722f * c.b;
    }
}
