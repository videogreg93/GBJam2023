package gaia.managers.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import gaia.managers.assets.AssetManager.Companion.get
import gaia.managers.context.MainContext
import gaia.utils.addAsInput
import ktx.app.KtxInputAdapter
import ktx.log.debug
import ktx.log.info
import java.io.Console

// TODO maybe separate all of these classes into different files
class InputActionManager : KtxInputAdapter {
    internal val actionListeners = ArrayList<ActionListener>()
    private val disabledInputs = ArrayList<ActionListener.InputAction>()
    var isUsingController = false

    /**
     * Will disable all inputs, irregardless of their presence in [disabledInputs]
     */
    var inputEnabled = true

    fun init() {
        Gdx.input.inputProcessor = this
//        controllerManager.init()
    }



    /**
     * Latest subscribers get priority
     */
    fun subscribe(listener: ActionListener) {
        actionListeners.add(0, listener)
    }

    override fun keyDown(keycode: Int): Boolean {
        return onKey(keycode, false)
    }

    override fun keyUp(keycode: Int): Boolean {
        return onKey(keycode, true)
    }

    fun disableInputs(vararg keycodes: ActionListener.InputAction) {
        disabledInputs.addAll(keycodes.toList())
    }

    fun enableInputs(vararg keycodes: ActionListener.InputAction) {
        disabledInputs.removeAll(keycodes.toList())
    }

    fun disableAllInputs() {
        info { "disabling all inputs" }
        disabledInputs.addAll(ActionListener.InputAction.values())
    }

    fun enableAllInputs() {
        info { "Enabling all inputs" }
        disabledInputs.clear()
    }

    private fun onKey(keycode: Int, isReleased: Boolean): Boolean {
        if (!inputEnabled) return false
        isUsingController = false
        val action = keyboardMappings[keycode]
        if (disabledInputs.contains(action)) return false
        action?.let {
            actionListeners.forEach { listener ->
                if (isReleased) {
                    if (listener.onActionReleased(action)) return true
                } else {
                    if (listener.onAction(action)) return true
                }
            }
        } ?: debug { "No Action mapped to $keycode" }
        return false
    }

    /**
     * Manually trigger an action programatically. Can be useful for things like tutorials where we want to simulate
     * a player triggering certain keypresses. Will bypasse [disabledInputs].
     */
    fun forceAction(action: ActionListener.InputAction, isReleased: Boolean): Boolean {
        actionListeners.forEach { listener ->
            if (isReleased) {
                if (listener.onActionReleased(action)) return true
            } else {
                if (listener.onAction(action)) return true
            }
        }
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!inputEnabled) return false
        isUsingController = false
        val action = if (button == 0) ActionListener.InputAction.CLICK else ActionListener.InputAction.RIGHT_CLICK
        if (disabledInputs.contains(action)) return false
        actionListeners.forEach { listener ->
            if (listener.onAction(action)) return true
        }
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!inputEnabled) return false
        isUsingController = false
        val action = if (button == 0) ActionListener.InputAction.CLICK else ActionListener.InputAction.RIGHT_CLICK
        if (disabledInputs.contains(action)) return false
        actionListeners.forEach { listener ->
            if (listener.onActionReleased(action)) return true
        }
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return super.mouseMoved(screenX, screenY)
    }

    fun unsubscribe(listener: ActionListener) {
        actionListeners.remove(listener)
    }

    /**
     * Returns the [Int] from Keys.Input mapped to the action [action]
     */
    fun inputForAction(action: ActionListener.InputAction): Int {
        return keyboardMappings.toList().first {
            it.second == action
        }.first
    }

    fun inputNameForAction(action: ActionListener.InputAction): String {
        val input = inputForAction(action)
        return Input.Keys.toString(input)
    }

    fun disableAllInputsAction(): Action = Actions.run {
        disableAllInputs()
    }

    fun enableAllInputsAction(): Action = Actions.run {
        enableAllInputs()
    }

//    fun smallVibration() {
//        controllerManager.currentController?.let {
//            if (it?.canVibrate() == true) {
//                it.startVibration(100, 0.8f)
//            }
//        }
//    }

    fun inputAssetFor(input: Int): Texture {
        return when (input) {
            Input.Keys.ESCAPE -> assets["esc"]
            Input.Keys.SPACE -> assets["space"]
            Input.Keys.BACKSPACE -> assets["backspace"]
            Input.Keys.UP -> assets["up"]
            Input.Keys.DOWN -> assets["down"]
            Input.Keys.LEFT -> assets["left"]
            Input.Keys.RIGHT -> assets["right"]
            Input.Keys.Z -> assets["z"]
            Input.Keys.A -> assets["a"]
            Input.Keys.S -> assets["s"]
            Input.Keys.D -> assets["d"]
            Input.Keys.X -> assets["x"]
            Input.Keys.C -> assets["c"]
            Input.Keys.Q -> assets["q"]
            else -> assets["unknown"]
        }!!.get()
    }

    fun inputAssetForAction(action: ActionListener.InputAction) = inputAssetFor(inputForAction(action))

    val keyboardMappings = hashMapOf<Int, ActionListener.InputAction>()
    val defaultKeyboardMappings = HashMap<Int, ActionListener.InputAction>().apply {
        put(Input.Keys.UP, ActionListener.InputAction.UP)
        put(Input.Keys.DOWN, ActionListener.InputAction.DOWN)
        put(Input.Keys.LEFT, ActionListener.InputAction.LEFT)
        put(Input.Keys.RIGHT, ActionListener.InputAction.RIGHT)
        put(Input.Keys.NUM_1, ActionListener.InputAction.ONE)
        put(Input.Keys.NUM_2, ActionListener.InputAction.TWO)
        put(Input.Keys.NUM_3, ActionListener.InputAction.THREE)
        put(Input.Keys.NUM_4, ActionListener.InputAction.FOUR)
    }

    val inputMappings: HashMap<Int, ActionListener.InputAction>
        get() = keyboardMappings

    fun loadKeyboardMappings() {
        keyboardMappings.clear()
        keyboardMappings.putAll(defaultKeyboardMappings)
    }

    companion object {
        private val assetsName = arrayOf(
            "a",
            "s",
            "d",
            "z",
            "x",
            "c",
            "q",
            "dpad_all",
            "esc",
            "space",
            "up",
            "down",
            "left",
            "right",
            "backspace",
            "unknown"
        )

        // TODO this is fucking dirty
        val assets = HashMap<String, AssetDescriptor<Texture>>().apply {
            assetsName.forEach {
                this[it] = AssetDescriptor("controls/$it.png", Texture::class.java)
            }
        }
        val controllerAssets = hashMapOf(
            *listOf(
                "a", "b", "down", "dpad", "lb", "left", "lt", "rb", "right", "rt", "select", "start", "up", "x", "y"
            ).map {
                it to AssetDescriptor("controls/controller/${it}.png", Texture::class.java)
            }.toTypedArray()
        )
        val assetsPressed = HashMap<String, AssetDescriptor<Texture>>().apply {
            // TODO at one point we will use the whole alphabet
            listOf("a", "s", "d", "z", "x", "c", "dpad_all", "space").forEach {
                this[it] = AssetDescriptor("controls/${it}_pressed.png", Texture::class.java)
            }
        }
    }


}

interface ActionListener {
    fun onAction(action: InputAction): Boolean
    fun onActionReleased(action: InputAction): Boolean {
        return false
    }

    enum class InputAction {
        UP, DOWN, LEFT, RIGHT, ONE, TWO, THREE, FOUR, CLICK, RIGHT_CLICK
    }
}

val ActionListener.InputAction.prettyName: String
    get() = name.lowercase().capitalize().replace("_", " ")

