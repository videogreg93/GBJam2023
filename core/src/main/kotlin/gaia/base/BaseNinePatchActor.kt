package gaia.base

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.NinePatch

class BaseNinePatchActor(val ninePatch: NinePatch) : BaseActor() {

    override fun draw(batch: Batch, parentAlpha: Float) {
        ninePatch.draw(batch, x, y, width, height)
    }
}
