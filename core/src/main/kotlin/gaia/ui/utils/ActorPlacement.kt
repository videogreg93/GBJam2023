package gaia.ui.utils


import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import gaia.Globals
import gaia.Globals.WORLD_HEIGHT
import gaia.Globals.WORLD_WIDTH
import gaia.base.BaseActor
import gaia.ui.generic.Label
import gaia.utils.sumByFloat
import ktx.log.info

fun BaseActor.alignTop(margin: Float = 0f) {
    y = WORLD_HEIGHT / 2 - height + margin
}

fun BaseActor.alignBottom(margin: Float = 0f) {
    y = -WORLD_HEIGHT / 2 + margin
}

fun BaseActor.alignLeft(margin: Float = 0f) {
    x = -WORLD_WIDTH / 2 + margin
}

fun BaseActor.alignRight(margin: Float = 0f) {
    x = WORLD_WIDTH / 2 - width + margin
}

fun BaseActor.centerHorizontally() {
    x = -width / 2
}

fun BaseActor.centerVertically() {
    y = -height / 2
}

fun BaseActor.centerVerticallyOn(other: BaseActor) {
    centerVerticallyOn(other.y + other.height / 2)
}

/**
 * Labels have their 'y' position in the top left instead of bottom left, so we need a specific function for this
 */
fun BaseActor.centerVerticallyOn(other: Label) {
    centerVerticallyOn(other.y - other.height / 2f)
}

fun BaseActor.centerVerticallyOn(otherY: Float) {
    y = otherY - this.height / 2
}

fun BaseActor.centerHorizontallyOn(other: BaseActor) {
    centerHorizontallyOn(other.x + other.width / 2)
}

fun BaseActor.centerHorizontallyOn(otherX: Float) {
    x = otherX - width / 2
}

fun BaseActor.alignToTopOf(other: BaseActor, margin: Float = 0f) {
    y = other.y + other.height + margin
}

fun Label.alignTopToTopOf(other: BaseActor, margin: Float = 0f) {
    y = other.y + other.height + margin
}

fun BaseActor.alignTopToTopOf(other: BaseActor, margin: Float = 0f) {
    y = other.y + other.height - height + margin
}

fun BaseActor.alignTopToBottomOfLabel(other: Label, margin: Float = 0f) {
    y = other.y - other.height - height + margin
}

fun Label.alignTopToBottomOfLabel(other: Label, margin: Float = 0f) {
    y = other.y - other.height + margin
}

fun BaseActor.alignTopToBottomOf(other: BaseActor, margin: Float = 0f) {
    y = other.y - height + margin
}

fun Label.alignTopToBottomOf(other: BaseActor, margin: Float = 0f) {
    if (other is Label) alignTopToBottomOfLabel(other, margin)
    else y = other.y + margin
}

fun BaseActor.alignBottomToBottomOf(other: BaseActor, margin: Float = 0f) {
    y = other.y + margin
}

fun Label.alignBottomToBottomOf(other: BaseActor, margin: Float = 0f) {
    y = other.y + height + margin
}

fun BaseActor.alignBottomToTopOf(other: BaseActor, margin: Float = 0f) {
    y = other.y + other.height + margin
}

fun BaseActor.alignLeftToRightOf(other: BaseActor, margin: Float = 0f) {
    x = other.x + other.width + margin
}

fun BaseActor.alignRightToLeftOf(other: BaseActor, margin: Float = 0f) {
    x = other.x - width + margin
}

fun BaseActor.alignRightToRightOf(other: BaseActor, margin: Float = 0f) {
    x = other.x + other.width - width + margin
}

fun BaseActor.alignLeftToLeftOf(other: BaseActor, margin: Float = 0f) {
    x = other.x + margin
}

// TODO fix label stuff because this is getting out of hand
fun List<BaseActor>.centerVerticallyOn(other: BaseActor, verticalMargin: Float = 0f) {
//    val totalHeight = sumByFloat { it.height + spacing }
//    val startY = other.y + (other.height / 2f) + (totalHeight / 2f) - verticalMargin
    if (size == 1) {
        info { "List of size 1, use Center instead" }
        first().centerOn(other)
    } else {
        val totalHeight = other.height - (2 * verticalMargin)
        val spacing = (totalHeight - this.sumByFloat { it.height }) / (size - 1)
        forEachIndexed { index, baseActor ->
            val asLabel = (baseActor as? Label)
            when (index) {
                0 -> {
                    if (asLabel != null) {
                        asLabel.alignTopToTopOf(other, -verticalMargin)
                    } else {
                        baseActor.alignTopToTopOf(other, -verticalMargin)
                    }
                }
//                lastIndex -> {
//                    baseActor.alignBottomToBottomOf(other, verticalMargin)
//                }
                else -> {
                    if (asLabel != null) {
                        asLabel.alignTopToBottomOf(this[index - 1], -spacing)
                    } else {
                        baseActor.alignTopToBottomOf(this[index - 1], -spacing)
                    }
                }
            }
        }
    }
}

// Offscreen

fun BaseActor.setOffscreenTop() {
    y = Globals.WORLD_HEIGHT / 2f
}

/**
 * Lets you move an actor around to calculate a position. Returns the new position, but also places the actor
 * back into its original position.
 */
fun <T : BaseActor> T.calculatePositionFor(dsl: T.() -> Unit): Vector2 {
    val currentPosition = Vector2(x, y)
    dsl()
    val newPosition = Vector2(x, y)
    setPosition(currentPosition.x, currentPosition.y)
    return newPosition
}

fun SequenceAction.skip() {
    val indexField = SequenceAction::class.java.getDeclaredField("index")
    indexField.isAccessible = true
    val currentIndex = indexField.getInt(this)
    indexField.setInt(this, currentIndex + 1)
}

