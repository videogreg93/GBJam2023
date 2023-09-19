package com.odencave.screens

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.assets.Assets
import com.odencave.entities.player.Player
import com.odencave.i18n.gaia.base.BackgroundGrid
import com.odencave.i18n.gaia.ui.shaders.Shaders
import com.odencave.i18n.screens.MainScreen
import gaia.Globals
import gaia.base.BaseActor
import gaia.managers.MegaManagers
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get
import gaia.managers.input.ActionListener
import gaia.ui.BasicScreen
import gaia.ui.generic.Label
import gaia.ui.utils.*
import kotlin.math.min
import kotlin.math.sin

class TitleScreen() : BasicScreen("Title") {

    val titleLabel = Label("Zenith", MegaManagers.fontManager.titleFont)
    val pressStartLabel = Label("Press Start", MegaManagers.fontManager.titleFont, 200f, 200f)
    val lineLeft = BaseActor(lineAsset.get())
    val lineRight = BaseActor(lineAsset.get())

    val letterboxTop = BaseActor(letterboxAsset.get())
    val letterboxBottom = BaseActor(letterboxAsset.get())

    val background = BackgroundGrid(backgroundAsset.get())
    var canContinue = false

    val fakePlayer = BaseActor(Player.playerTexture.get(), 0f, -1000f).apply {
        center()
        alignLeft(-20f)
    }
    val middlePlayerY = fakePlayer.y - 14f

    init {
        backgroundCrew.addMember(background)
        titleLabel.center()
        titleLabel.alignTop(-40f)
        lineLeft.addForeverAction {
            lineLeft.centerOn(titleLabel)
            lineLeft.y -= titleLabel.height
            lineLeft.alignRightToLeftOf(titleLabel, -10f)
        }
        lineRight.addForeverAction {
            lineRight.centerOn(titleLabel)
            lineRight.y -= titleLabel.height
            lineRight.alignLeftToRightOf(titleLabel, 10f)
        }
        letterboxTop.center()
        letterboxTop.y = Globals.WORLD_HEIGHT / 2f
        letterboxBottom.center()
        letterboxBottom.y = -Globals.WORLD_HEIGHT / 2f - letterboxBottom.height

        pressStartLabel.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.delay(1f, Actions.run {
                        pressStartLabel.shouldDraw = false
                    }),
                    Actions.delay(1f, Actions.run {
                        pressStartLabel.shouldDraw = true
                    })
                )
            )
        )

        crew.addMembers(letterboxTop, letterboxBottom)
    }

    override fun firstShown() {
        super.firstShown()
        batch.shader = Shaders.paletteShader
        letterboxTop.addAction(
            Actions.sequence(
                Actions.moveBy(0f, -letterboxTop.height, 1.5f, Interpolation.fastSlow),
                Actions.delay(0.1f, Actions.run {
                    pressStartLabel.center()
                    pressStartLabel.alignBottomToTopOf(letterboxBottom, 4f)
                    crew.addMembers(titleLabel, lineLeft, lineRight, fakePlayer)
                    hudCrew.addMember(pressStartLabel)
                    canContinue = true
                })
            )
        )
        letterboxBottom.addAction(
            Actions.moveBy(0f, letterboxBottom.height, 1.5f, Interpolation.fastSlow)
        )
    }

    var deltaAcc = 0f

    override fun render(delta: Float) {
        super.render(delta)
        val colors = Globals.selectedPalette.colorsSortedByLightness()
        batch.shader.setUniformf("inputColor1", colors[0])
        batch.shader.setUniformf("inputColor2", colors[1])
        batch.shader.setUniformf("inputColor3", colors[2])
        batch.shader.setUniformf("inputColor4", colors[3])
        deltaAcc += delta
        fakePlayer.x += 8 * delta
        fakePlayer.x = min(fakePlayer.x, -fakePlayer.width / 2f)
        fakePlayer.y = middlePlayerY + sin(deltaAcc * 3) * 12f
    }

    override fun onAction(action: ActionListener.InputAction): Boolean {
        when (action) {
            ActionListener.InputAction.ZERO -> {
                Globals.selectedPaletteIndex++
                Globals.currentBackgroundColor = Globals.selectedPalette.color4
            }

            else -> {
                if (canContinue) {
                    MegaManagers.screenManager.changeScreen(MainScreen())
                }
            }
        }
        return true
    }

    companion object {
        @Asset
        private val lineAsset = AssetDescriptor(Assets.Title.line, Texture::class.java)

        @Asset
        private val letterboxAsset = AssetDescriptor(Assets.Title.letterbox, Texture::class.java)

        @Asset
        private val backgroundAsset = AssetDescriptor(Assets.Backgrounds.title, Texture::class.java)
    }
}
