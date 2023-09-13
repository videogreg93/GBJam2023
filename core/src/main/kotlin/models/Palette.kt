package com.odencave.i18n.models

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3

/**
 * Contains 4 colors, from dark to light
 */
data class Palette(
    val color1: Vector3,
    val color2: Vector3,
    val color3: Vector3,
    val color4: Vector3,
) {
    companion object {
        val palette1 = Palette(
            Vector3(0.031f, 0.094f, 0.125f),
            Vector3(0.204f, 0.408f, 0.337f),
            Vector3(0.533f, 0.753f, 0.439f),
            Vector3(0.878f, 0.973f, 0.816f),
        )

        val palette2 = Palette(
            Vector3(0.094f, 0.106f, 0.141f),
            Vector3(0.047f, 0.314f, 0.4f),
            Vector3(0.851f, 0.592f, 0.255f),
            Vector3(0.941f, 0.882f, 0.82f),
        )

        private val palette3 = Palette(
            Vector3(0.133f, 0.027f, 0.502f),
            Vector3(0.38f, 0.373f, 0.929f),
            Vector3(1f, 0.729f, 0.353f),
            Vector3(1f, 0.98f, 0.698f),
        )

        private val palette4 = Palette(
            Vector3(0f,0.169f,0.349f),
            Vector3(0f,0.373f,0.549f),
            Vector3(0f,0.725f,0.745f),
            Vector3(0.624f,0.957f,0.898f),
        )

        val allPalettes = listOf(palette1, palette2, palette3, palette4)
    }
}
