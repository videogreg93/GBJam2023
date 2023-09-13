#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float grayscale;

void main()
{
    vec4 texColor = texture2D(u_texture, v_texCoords);
    float texAlpha = texColor.a;
    // Grayscale colors, from dark to lighter
    vec4 color1 = vec4(0.125, 0.125, 0.125, 1);
    vec4 color2 = vec4(0.376, 0.376, 0.376, 1);
    vec4 color3 = vec4(0.624, 0.624, 0.624, 1);
    vec4 color4 = vec4(0.875, 0.875, 0.875, 1);

    // Palette colors, from dark to lighter
    vec4 paletteColor1 = vec4(0.133,0.027,0.502, 1);
    vec4 paletteColor2 = vec4(0.38,0.373,0.929, 1);
    vec4 paletteColor3 = vec4(1,0.729,0.353, 1);
    vec4 paletteColor4 = vec4(1,0.98,0.698, 1);



    if (texAlpha > 0) {
        if (texColor.r <= 0.126) {
            gl_FragColor = paletteColor1;
        } else if (texColor.r <= 0.377) {
            gl_FragColor = paletteColor2;
        } else if (texColor.r <= 0.625) {
            gl_FragColor = paletteColor3;
        } else {
            gl_FragColor = paletteColor4;
        }
    } else {
        gl_FragColor = texColor;
    }
}
