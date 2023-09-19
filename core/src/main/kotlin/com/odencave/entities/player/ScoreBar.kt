package com.odencave.entities.player

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.odencave.ScoreManager
import com.odencave.assets.Assets
import gaia.base.BaseActor
import gaia.managers.MegaManagers
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get
import gaia.utils.FloatLerpAction.Companion.createLerpAction

class ScoreBar : BaseActor(barFrameAsset.get()) {
    private val scoreManager by lazy { MegaManagers.getManager<ScoreManager>() }
    private var shownPercentage = 0f
    var actualFillPercent = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            addAction(createLerpAction(shownPercentage, actualFillPercent, 0.4f, Interpolation.circleOut) { currentValue ->
                shownPercentage = currentValue
            })
        }
    private val barFillingTexture = barFillingAsset.get()
    private val loadingBarTextureRegion = TextureRegion(barFillingTexture)

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(loadingBarTextureRegion, x + 1, y + 1)
        super.draw(batch, parentAlpha)
    }


    override fun act(delta: Float) {
        super.act(delta)
        loadingBarTextureRegion.regionWidth = (barFillingTexture.width * shownPercentage).toInt()
        val tempPercent = (scoreManager.upgradeScore.toFloat()) / (scoreManager.nextUpgrade.scoreThreshold.toFloat())
        if (tempPercent != actualFillPercent) {
            actualFillPercent = tempPercent
        }
    }

    companion object {
        @Asset
        val barFrameAsset = AssetDescriptor(Assets.Player.scoreBarFrame, Texture::class.java)

        @Asset
        val barFillingAsset = AssetDescriptor(Assets.Player.scoreBarFill, Texture::class.java)
    }
}
