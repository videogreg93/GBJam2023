#version 330
#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

// Input variables
uniform vec2 u_resolution;
uniform sampler2D transition;
uniform float cutoff;
uniform int inverse;
void main()
{
    // Get the fragment position (ranging from 0-1)
    vec2 uv = gl_FragCoord.xy / u_resolution.xy;

    // Flip the y-axis so that the texture is right-side up
    uv = vec2(uv.x, 1.0 - uv.y);

    // Get the fragment color at the specified uv position
    vec4 fragmentColor = texture(u_texture, v_texCoords);
    vec4 transitionColor = texture(transition, uv);

    float time = sin(mix(0.0, 6.16, cutoff));
    float transitionRed = transitionColor.r;
    if (inverse == 1) {
        transitionRed = 1.0 - transitionColor.r;
    }

    if (transitionRed < time) {
        gl_FragColor = vec4(0.0,0.0,0.0,1.0);
    } else {
        gl_FragColor = fragmentColor;
    }

}