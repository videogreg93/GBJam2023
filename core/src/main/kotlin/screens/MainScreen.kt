package com.odencave.i18n.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.i18n.gaia.ui.shaders.Shaders
import gaia.Globals
import gaia.base.BaseActor
import gaia.managers.input.ActionListener
import gaia.ui.BasicScreen
import gaia.ui.utils.addForeverAction

class MainScreen: BasicScreen("Main") {

    override fun firstShown() {
        super.firstShown()
        val head = BaseActor(Texture("head.png")).apply {
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

    override fun onAction(action: ActionListener.InputAction): Boolean {
        println(action)
        when (action) {
            ActionListener.InputAction.ONE -> updateResolution(1)
            ActionListener.InputAction.TWO -> updateResolution(2)
            ActionListener.InputAction.THREE -> updateResolution(4)
            ActionListener.InputAction.FOUR -> updateResolution(8)
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
