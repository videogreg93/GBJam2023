#version 330
#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

// Input variables
uniform vec2 u_resolution;
void main()
{
    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
}