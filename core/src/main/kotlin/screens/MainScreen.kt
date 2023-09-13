package com.odencave.i18n.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.i18n.gaia.ui.shaders.Shaders
import com.odencave.i18n.models.Palette
import gaia.Globals
import gaia.base.BaseActor
import gaia.managers.input.ActionListener
import gaia.ui.BasicScreen
import gaia.utils.wrappingCursor

class MainScreen : BasicScreen("Main") {

    private var selectedPaletteIndex: Int = 0
        set(value) {
            field = Palette.allPalettes.wrappingCursor(value)
        }
    private val selectedPalette: Palette
        get() = Palette.allPalettes[selectedPaletteIndex]
    override fun firstShown() {
        super.firstShown()
        batch.shader = Shaders.paletteShader
        val head = BaseActor(Texture("testPalette.png")).apply {
            center()
            addAction(
                Actions.forever(
                    Actions.sequence(
                        Actions.moveBy(50f, 0f, 2f),
                        Actions.moveBy(-100f, 0f, 4f),
                        Actions.moveBy(50f, 0f, 2f),
                    )
                )
            )
        }
        crew.addMembers(head)
    }

    override fun render(delta: Float) {
        super.render(delta)
        val colors = selectedPalette.colorsSortedByLightness()
        batch.shader.setUniformf("inputColor1", colors[0])
        batch.shader.setUniformf("inputColor2", colors[1])
        batch.shader.setUniformf("inputColor3", colors[2])
        batch.shader.setUniformf("inputColor4", colors[3])
    }

    override fun onAction(action: ActionListener.InputAction): Boolean {
        println(action)
        when (action) {
            ActionListener.InputAction.ONE -> updateResolution(1)
            ActionListener.InputAction.TWO -> updateResolution(2)
            ActionListener.InputAction.THREE -> updateResolution(4)
            ActionListener.InputAction.FOUR -> updateResolution(8)
            ActionListener.InputAction.ZERO -> {
                selectedPaletteIndex++
                Globals.currentBackgroundColor = selectedPalette.color4
            }

            else -> return false
        }
        return true
    }

    private fun updateResolution(multiplier: Int) {
        Gdx.graphics.setWindowedMode(
            (Globals.WORLD_WIDTH * multiplier).toInt(),
            (Globals.WORLD_HEIGHT * multiplier).toInt()
        )
    }
}
