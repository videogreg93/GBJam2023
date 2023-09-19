package com.odencave.i18n.screens

import com.odencave.screens.TitleScreen
import gaia.Globals
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
            if (Globals.skipIntro) {
                MegaManagers.screenManager.changeScreen(MainScreen())
            } else {
                MegaManagers.screenManager.changeScreen(TitleScreen())
            }
        }
    }
}
