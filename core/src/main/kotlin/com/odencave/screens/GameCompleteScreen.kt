package com.odencave.screens

import com.odencave.ScoreManager
import com.odencave.i18n.gaia.base.BackgroundGrid
import gaia.Globals
import gaia.managers.MegaManagers
import gaia.managers.assets.AssetManager.Companion.get
import gaia.managers.input.ActionListener
import gaia.ui.BasicScreen
import gaia.ui.generic.FlowLabel
import gaia.ui.generic.Label
import gaia.ui.utils.alignTop
import gaia.ui.utils.calculatePositionFor
import kotlin.math.cos
import kotlin.math.sin

class GameCompleteScreen: BasicScreen("Game Over") {

    private val tex = "And so, thanks to the brave efforts of the Zenith and its crew members, The Sun and all of its warriors have been destroyed. Thank you for your service."

    private val gameCompleteLabel = FlowLabel(tex, wrapSize = (Globals.WORLD_WIDTH - 10).toInt(), font = MegaManagers.fontManager.defaultFont).apply {
        center()
        alignTop(-10f)
    }

    private val finalScoreLabel = Label("Final Score: ${MegaManagers.getManager<ScoreManager>().currentTotalScore}", MegaManagers.fontManager.defaultFont)

    var accDelta = 0f

    override fun firstShown() {
        super.firstShown()
        val backgroundGrid = BackgroundGrid(DeathScreen.backgroundAsset.get(), 0, 0)
        backgroundCrew.addMember(backgroundGrid)
        gameCompleteLabel.center()
        crew.addMember(gameCompleteLabel)
    }

    override fun render(delta: Float) {
        super.render(delta)
        accDelta += delta
        if (accDelta >= 10f) {
            gameCompleteLabel.removeFromCrew()
            finalScoreLabel.center()
            crew.addMember(finalScoreLabel)
        }
    }

    override fun onAction(action: ActionListener.InputAction): Boolean {
        return true
    }
}
