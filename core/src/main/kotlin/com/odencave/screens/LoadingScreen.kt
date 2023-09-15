package com.odencave.i18n.screens

import gaia.managers.MegaManagers
import gaia.managers.input.ActionListener
import gaia.ui.BasicScreen

class LoadingScreen : BasicScreen("Loading") {

    override fun onAction(action: ActionListener.InputAction): Boolean {
        return false
    }

    override fun render(delta: Float) {
        super.render(delta)
        if (MegaManagers.assetManager.update()) {
            MegaManagers.screenManager.changeScreen(MainScreen())
        }
    }
}
