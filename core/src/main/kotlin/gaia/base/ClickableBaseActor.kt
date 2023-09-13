package gaia.base

import com.badlogic.gdx.graphics.Texture

/**
 * For creating 'anonymous' clickable base actors
 */
open class ClickableBaseActor(texture: Texture?, var clickAction: () -> Boolean = DEFAULT_CALLBACK) :
    BaseActor(texture),
    Clickable {
    var rightclickAction: () -> Boolean = DEFAULT_CALLBACK
    override fun onClick(): Boolean = clickAction()
    open fun onRightClick(): Boolean = rightclickAction()

    companion object {
        private val DEFAULT_CALLBACK: () -> Boolean = { false }
    }
}
