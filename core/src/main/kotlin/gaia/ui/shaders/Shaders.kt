package com.odencave.i18n.gaia.ui.shaders

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import ktx.log.info

/**
 * White shader found here: https://badlogicgames.com/forum/viewtopic.php?f=11&t=13239
 */
object Shaders {

    val whiteVertexShader = "attribute vec4 a_position; \n" +
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0; \n" +
            "uniform mat4 u_projTrans; \n" +
            "varying vec4 v_color; \n" +
            "varying vec2 v_texCoords; \n" +
            "void main() { \n" +
            "v_color = a_color; \n" +
            "v_texCoords = a_texCoord0; \n" +
            "gl_Position = u_projTrans * a_position; \n" +
            "};"

    val whiteFragmentShader = "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" + "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "uniform float grayscale;\n" +
            "void main()\n" +
            "{\n" +
            "vec4 texColor = texture2D(u_texture, v_texCoords);\n" +
            "float gray = dot(texColor.rgb, vec3(5, 5, 5));\n" +
            "texColor.rgb = mix(vec3(gray), texColor.rgb, grayscale);\n" +
            " gl_FragColor = v_color * texColor;\n" +
            "}"
    val whiteShader: ShaderProgram
        get() = ShaderProgram(whiteVertexShader, whiteFragmentShader)
    // https://lospec.com/palette-list/nintendo-gameboy-bgb
    val palette1: ShaderProgram
        get() = ShaderProgram(
            Gdx.files.internal("shaders/passthrough.vert"),
            Gdx.files.internal("shaders/palette1.frag"),
        )
    // https://lospec.com/palette-list/modern-4
    val palette2: ShaderProgram
        get() = ShaderProgram(
            Gdx.files.internal("shaders/passthrough.vert"),
            Gdx.files.internal("shaders/palette2.frag"),
        )
    // https://lospec.com/palette-list/cosmic-cream-gb
    val palette3: ShaderProgram
        get() = ShaderProgram(
            Gdx.files.internal("shaders/passthrough.vert"),
            Gdx.files.internal("shaders/palette3.frag"),
        )
    val transitionShader: ShaderProgram
        get() = ShaderProgram(
            Gdx.files.internal("shaders/passthrough.vert"),
            Gdx.files.internal("shaders/leftToRightTransition.frag")
        )
    val fadeInShader: ShaderProgram
        get() = ShaderProgram(fadeInShaderVertex, fadeInShaderFragment)

    lateinit private var fadeInShaderVertex: FileHandle
    lateinit private var fadeInShaderFragment: FileHandle

    fun initShaders() {
        fadeInShaderVertex = Gdx.files.internal("shaders/passthrough.vert")
        fadeInShaderFragment = Gdx.files.internal("shaders/fadeIn.frag")
        val allShaders = listOf(transitionShader, palette1, palette2, palette3)
        allShaders.forEach {
            if (it.isCompiled) {
                info { "$it compiled" }
            } else {
                ktx.log.error { it.log }
            }
        }
    }
}
