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
    vec4 redColor = texture(u_texture, uv - vec2(0.05, 0));
    vec4 greenColor = texture(u_texture, uv);
    vec4 blueColor = texture(u_texture, uv + vec2(0.05, 0));
    gl_FragColor = vec4(redColor.r, greenColor.g, blueColor.b, (redColor.a+greenColor.a+blueColor.a));
}