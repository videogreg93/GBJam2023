package gaia.ui.generic

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.SFX
import gaia.managers.MegaManagers
import gaia.managers.assets.AssetManager.Companion.get
import gaia.managers.input.ActionListener
import gaia.utils.getHeight
import gaia.utils.getWidth
import gaia.utils.wrapped
import kotlin.math.min

/**
 * Like a [Label], but the text appears character by character instead of appearing all at once
 */
class FlowLabel(startingCompleteText: String,
                var textSpeed: Float = DEFAULT_TEXT_SPEED,
                val wrapSize: Int = 0,
                font: BitmapFont,
                x: Float = 0f,
                y: Float = 0f) : Label("", font, x, y), ActionListener {
    var completeText: String = startingCompleteText
        set(value) {
            field = value
            textAction = setupAction()
        }
    private var charactersToShow = 0
    private var textAction: Action? = null

    val textFinished: Boolean
        get() = text == completeText.wrapped(font, wrapSize)

    val textSound = SFX.textAdvance.get()


    override fun onAction(action: ActionListener.InputAction): Boolean {
        when (action) {
            ActionListener.InputAction.SHOOT, ActionListener.InputAction.START -> {
                removeAction(textAction)
                textSound.stop()
                text = completeText.wrapped(font, wrapSize)
                MegaManagers.inputActionManager.unsubscribe(this)
            }
            else -> {
                return false
            }
        }
        return true
    }

    override fun getWidth(): Float {
        return font.getWidth(completeText.wrapped(font, wrapSize))
    }

    override fun getHeight(): Float {
        val lineCount = completeText.wrapped(font, wrapSize).split("\n").size
        return font.getHeight(completeText) * lineCount
    }

    private fun setupAction(): Action {
        charactersToShow = 0
        textAction?.let {
            removeAction(it)
        }
        val action = Actions.sequence(Actions.run {
            MegaManagers.soundManager.loopSFX(textSound, -0.3f)
        }, Actions.repeat(completeText.length, Actions.delay(textSpeed, Actions.run {
            charactersToShow++
            text = completeText.substring(0, min(charactersToShow, completeText.length))
            wrap(wrapSize)
        })), Actions.run {
            textSound.stop()
            MegaManagers.inputActionManager.unsubscribe(this)
        })
        addAction(action)
        MegaManagers.inputActionManager.subscribe(this)
        return action
    }

    companion object {
        const val DEFAULT_TEXT_SPEED = 0.0075f
    }
}
