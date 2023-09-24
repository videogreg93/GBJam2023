package com.odencave.screens

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.ScoreManager
import com.odencave.i18n.gaia.base.BackgroundGrid
import gaia.Globals
import gaia.managers.MegaManagers
import gaia.managers.assets.AssetManager.Companion.get
import gaia.managers.input.ActionListener
import gaia.ui.BasicScreen
import gaia.ui.generic.Label
import gaia.utils.wrapped

class GameCompleteScreen : BasicScreen("Game Over") {

    private val tex = "And so, thanks to the brave efforts of the Zenith and its crew members, The Sun and all of its warriors have been destroyed. Thank you for your service.".wrapped(MegaManagers.fontManager.defaultFont, Globals.WORLD_WIDTH.toInt() - 20)

    private val gameCompleteLabel = Label("", MegaManagers.fontManager.defaultFont)

    private val finalScoreLabel = Label("Final Score: ${MegaManagers.getManager<ScoreManager>().currentTotalScore}", MegaManagers.fontManager.defaultFont)

    var accDelta = 0f

    override fun firstShown() {
        super.firstShown()
        val backgroundGrid = BackgroundGrid(DeathScreen.backgroundAsset.get(), 0, 0)
        backgroundCrew.addMember(backgroundGrid)
        gameCompleteLabel.setPosition(
            (-Globals.WORLD_WIDTH/2) + 10, 0f
        )
        crew.addMember(gameCompleteLabel)
        gameCompleteLabel.addAction(
            Actions.sequence(
                Actions.repeat(tex.length, Actions.delay(0.1f, Actions.run {
                    val currentLength = gameCompleteLabel.text.length
                    gameCompleteLabel.text = tex.substring(0, currentLength + 1)
                })),
                Actions.delay(3f, Actions.run {
                    gameCompleteLabel.text = ""
                }),
                Actions.delay(1f, Actions.run {
                    gameCompleteLabel.removeFromCrew()
                    finalScoreLabel.center()
                    crew.addMember(finalScoreLabel)
                    finalScoreLabel.addAction(Actions.delay(10f, Actions.run {
                        MegaManagers.screenManager.changeScreen(LeaderboardScreen())
                    }))
                })
            )
        )
    }

    override fun onAction(action: ActionListener.InputAction): Boolean {
        return true
    }
}
