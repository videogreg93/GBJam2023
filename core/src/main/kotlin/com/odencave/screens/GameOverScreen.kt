package com.odencave.screens

import com.odencave.i18n.gaia.base.BackgroundGrid
import gaia.Globals
import gaia.managers.MegaManagers
import gaia.managers.assets.AssetManager.Companion.get
import gaia.managers.input.ActionListener
import gaia.ui.BasicScreen
import gaia.ui.generic.Label
import gaia.ui.utils.calculatePositionFor
import kotlin.math.cos
import kotlin.math.sin

class GameOverScreen : BasicScreen("Game Over") {

    private val gameOverLabel = Label("Game Over", MegaManagers.fontManager.defaultFont)

    var accDelta = 0f

    override fun firstShown() {
        super.firstShown()
        val backgroundGrid = BackgroundGrid(DeathScreen.backgroundAsset.get(), 0, 0)
        backgroundCrew.addMember(backgroundGrid)
        gameOverLabel.center()
        crew.addMember(gameOverLabel)
    }

    override fun render(delta: Float) {
        super.render(delta)
        accDelta += delta
        val centerPoint = gameOverLabel.calculatePositionFor {
            center()
        }
        gameOverLabel.x = centerPoint.x + 20 + (sin(accDelta * 2) * 20) - 20f
        gameOverLabel.y = (centerPoint.y + 20 + (cos(accDelta * 2) * 20)) - 20f
        if (accDelta >= 1f) {
//            Globals.resetForNewGame()
            MegaManagers.screenManager.changeScreen(LeaderboardScreen())
        }
    }

    override fun onAction(action: ActionListener.InputAction): Boolean {
        return true
    }
}
