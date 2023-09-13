#version 330
attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
uniform mat4 u_projTrans;
varying vec4 v_color;
varying vec2 v_texCoords;

// Input variables
uniform vec2 u_resolution;
uniform sampler2D transition;
uniform float cutoff;

uniform float pixelSize;
void main()
{
    v_color = vec4(1, 1, 1, 1);
    v_texCoords = a_texCoord0;
    gl_Position =  u_projTrans * a_position;
}