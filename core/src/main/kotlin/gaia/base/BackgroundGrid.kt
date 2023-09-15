package com.odencave.i18n.gaia.base

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.odencave.assets.Assets
import gaia.Globals.WORLD_HEIGHT
import gaia.Globals.WORLD_WIDTH
import gaia.base.BaseActor
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get
import gaia.ui.utils.withColor

class BackgroundGrid(
    gridTexture: Texture = space.get(),
    var speedX: Int = 32,
    var speedY: Int = 0,
    val posX: Float = -WORLD_WIDTH,
    val posY: Float = -WORLD_HEIGHT

) : BaseActor() {

    private val textureRegion = TextureRegion(gridTexture.apply {
        setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    }, (1920 * 2), (1080 * 3))

    init {
        drawIndex = 2000
    }

    var currentOffsetX = 0f
    var currentOffsetY = 0f

    override fun act(delta: Float) {
        super.act(delta)
        currentOffsetX += (delta * speedX)
        currentOffsetY += (delta * speedY)
        textureRegion.setRegion(
            currentOffsetX.toInt(),
            currentOffsetY.toInt(),
            (WORLD_WIDTH * 1.5).toInt(),
            (WORLD_HEIGHT * 1.5).toInt()
        )
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.withColor(color) {
            it.draw(textureRegion, posX, posY)
        }
    }

    companion object {
        @Asset
        val space = AssetDescriptor(Assets.Backgrounds.one, Texture::class.java)
    }
}
