package gaia.base

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion

open class BaseTextureRegionActor(textureRegion: TextureRegion, x: Float = 0f, y: Float = 0f) : BaseActor(null, x, y) {

    init {
        sprite = Sprite(textureRegion).apply {
            setPosition(x, y)
        }
        this.setSize(textureRegion.regionWidth.toFloat(), textureRegion.regionHeight.toFloat())
    }
}
