package com.odencave.screens

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.assets.Assets
import com.odencave.entities.player.Player
import com.odencave.i18n.gaia.base.BackgroundGrid
import com.odencave.i18n.screens.MainScreen
import gaia.Globals
import gaia.managers.MegaManagers
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get
import gaia.managers.input.ActionListener
import gaia.ui.BasicScreen
import gaia.ui.generic.Label
import gaia.ui.utils.addForeverAction

class DeathScreen(val player: Player) : BasicScreen("Death Screen") {

    private val lifeLabel = Label("x${Globals.currentLives}", MegaManagers.fontManager.defaultFont)

    override fun firstShown() {
        super.firstShown()
        lifeLabel.center()
        val backgroundGrid = BackgroundGrid(backgroundAsset.get(), 0, 0)
        backgroundCrew.addMember(backgroundGrid)
        crew.addMember(lifeLabel)
        lifeLabel.addForeverAction {
            lifeLabel.text = "x${Globals.currentLives}"
        }
        lifeLabel.addAction(
            Actions.sequence(
                Actions.delay(1f, Actions.run {
                    Globals.currentLives--
                }),
                Actions.delay(1.5f, Actions.run {
                    player.resetHealth()
                    if (Globals.currentLives > 0) {
                        MegaManagers.screenManager.changeScreen(MainScreen(player))
                    } else {
                        MegaManagers.screenManager.changeScreen(GameOverScreen())
                    }
                })
            )
        )
    }

    override fun onAction(action: ActionListener.InputAction): Boolean {
        return true
    }

    companion object {
        @Asset
        val backgroundAsset = AssetDescriptor(Assets.Backgrounds.deathScreen, Texture::class.java)
    }
}
