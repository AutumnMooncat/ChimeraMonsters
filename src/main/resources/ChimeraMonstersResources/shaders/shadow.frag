varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_scale;//settings dot scale
uniform vec2 u_screenSize;//width, height
uniform vec2 u_mouse;
uniform float x_time;
uniform float angleR;

void main() {
    vec2 uv = gl_FragCoord.xy / u_screenSize;
    vec4 texColor = texture(u_texture, uv);
    texColor.rgb *= 0.5;
    texColor.a *= 0.75;
    gl_FragColor = texColor;
}