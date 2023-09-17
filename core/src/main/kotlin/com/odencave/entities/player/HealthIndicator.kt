package com.odencave.entities.player

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.odencave.assets.Assets
import gaia.base.BaseActor
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get

class HealthIndicator(val player: Player) : BaseActor() {
    private val heartTexture by lazy { heartAsset.get() }
    private val heartMargin = 2

    override fun draw(batch: Batch, parentAlpha: Float) {
        repeat(player.currentHealth) {
            batch.draw(heartTexture, x + (it * (heartMargin + heartTexture.width)), y)
        }
    }

    override fun getHeight(): Float {
        return heartTexture.height.toFloat()
    }

    override fun getWidth(): Float {
        return player.currentHealth * (heartTexture.width.toFloat() + heartMargin)
    }

    companion object {
        @Asset
        private val heartAsset = AssetDescriptor(Assets.Player.heart, Texture::class.java)
    }
}
