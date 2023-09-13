package gaia.utils

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.Sprite
import gaia.base.BaseActor

fun BitmapFont.drawCenteredVerticallyOn(text: CharSequence, x: Float, batch: Batch?, actor: BaseActor) {
    actor.sprite?.let { sprite ->
        drawCenteredVerticallyOn(text, x, batch, sprite)
    }
}

fun BitmapFont.drawCenteredVerticallyOn(text: CharSequence, x: Float, batch: Batch?, sprite: Sprite) {
    drawCenteredVerticallyOn(text, x, batch, sprite.y, sprite.height)
}

fun BitmapFont.drawCenteredVerticallyOn(text: CharSequence, x: Float, batch: Batch?, targetY: Float, targetHeight: Float) {
    GlyphLayout(this, text).height.let { textHeight ->
        draw(batch, text, x, targetY + textHeight / 2 + targetHeight / 2)
    }
}

fun BitmapFont.drawCenteredHorizontallyOn(text: CharSequence, y: Float, batch: Batch?, actor: BaseActor) {
    actor.sprite?.let { sprite ->
        drawCenteredHorizontallyOn(text, y, batch, sprite)
    }
}

fun BitmapFont.drawCenteredHorizontallyOn(text: CharSequence, y: Float, batch: Batch?, sprite: Sprite) {
    drawCenteredHorizontallyOn(text, y, batch, sprite.x, sprite.width)
}

fun BitmapFont.drawCenteredHorizontallyOn(text: CharSequence, y: Float, batch: Batch?, targetX: Float, targetWidth: Float) {
    GlyphLayout(this, text).width.let { width ->
        draw(batch, text, targetX + targetWidth / 2 - width / 2, y)
    }
}

fun BitmapFont.drawCenteredOn(text: CharSequence, batch: Batch?, actor: BaseActor) {
    actor.sprite?.let { sprite ->
        drawCenteredOn(text, batch, sprite.x, sprite.y, sprite.width, sprite.height)
    }
}

fun BitmapFont.drawCenteredOn(text: CharSequence, batch: Batch?, targetX: Float, targetY: Float, targetWidth: Float, targetHeight: Float) {
    GlyphLayout(this, text).let { layout ->
        draw(batch, text, targetX + targetWidth / 2 - layout.width / 2, targetY + layout.height / 2 + targetHeight / 2)
    }
}

// Get size of drawn font
fun BitmapFont.getHeight(text: CharSequence): Float {
    return GlyphLayout(this, text).height
}

fun BitmapFont.getWidth(text: CharSequence): Float {
    return GlyphLayout(this, text).width
}


