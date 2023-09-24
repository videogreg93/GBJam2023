package com.odencave.entities.enemy.boss

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.odencave.SFX
import com.odencave.entities.player.ScoreBar.Companion.barFillingAsset
import com.odencave.entities.player.ScoreBar.Companion.barFrameAsset
import gaia.Globals
import gaia.base.BaseActor
import gaia.managers.MegaManagers
import gaia.managers.assets.AssetManager.Companion.get
import gaia.utils.FloatLerpAction.Companion.createLerpAction

class BossHealthBar(private val boss: Boss) : BaseActor(barFrameAsset.get()) {
    private var shownPercentage = 0f
    var initialLoad = true
    var actualFillPercent = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            if (initialLoad) {
                addAction(
                    createLerpAction(
                        shownPercentage,
                        actualFillPercent,
                        5f,
                        Interpolation.linear
                    ) { currentValue ->
                        shownPercentage = currentValue
                        initialLoad = false
                        MegaManagers.soundManager.playSFX(SFX.select.get(), -0.4f)
                    })
            } else {
                addAction(
                    createLerpAction(
                        shownPercentage,
                        actualFillPercent,
                        0.4f,
                        Interpolation.circleOut
                    ) { currentValue ->
                        shownPercentage = currentValue
                    })
            }
        }
    private val barFillingTexture = barFillingAsset.get()
    private val loadingBarTextureRegion = TextureRegion(barFillingTexture)

    override fun getWidth(): Float {
        return Globals.WORLD_WIDTH * 0.9f
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(sprite, x, y, width, sprite!!.height)
        batch.draw(loadingBarTextureRegion, x + 1, y + 1)
    }


    override fun act(delta: Float) {
        super.act(delta)
        loadingBarTextureRegion.regionWidth = ((width - 2) * shownPercentage).toInt()
        val tempPercent = (boss.currentHealth.toFloat()) / (boss.maxHealth.toFloat())
        if (tempPercent != actualFillPercent) {
            actualFillPercent = tempPercent
        }
        if (boss.currentHealth <= 0) {
            removeFromCrew()
        }
    }
}
