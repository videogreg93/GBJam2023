package gaia.ui


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import gaia.Globals.WORLD_HEIGHT
import gaia.Globals.WORLD_WIDTH
import gaia.actions.CameraShakeAction
import gaia.base.BaseActor
import gaia.base.Clickable
import gaia.base.Crew
import gaia.managers.MegaManagers
import gaia.managers.input.ActionListener
import ktx.app.KtxScreen
import ktx.log.info

/**
 * Default Screen. Simply logs the different lifecycle events
 */
abstract class BasicScreen(
    private val name: String
) : KtxScreen, ActionListener {
    internal val batch: SpriteBatch = MegaManagers.currentContext.inject()
    internal val hudBatch: SpriteBatch = MegaManagers.currentContext.inject()
    internal val camera: OrthographicCamera = OrthographicCamera()
    internal val hudCamera: OrthographicCamera = OrthographicCamera()
    internal val viewport: Viewport = FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera)
    internal val hudViewport: Viewport = FitViewport(WORLD_WIDTH, WORLD_HEIGHT, hudCamera)
    internal val crew = Crew(batch, camera)
    internal val hudCrew = Crew(hudBatch, hudCamera)
    private var isAlreadyShown = false

    // Background screenspace stuff
    internal val backgroundCrew = Crew(batch, hudCamera)

    // Dev tools
    //private val console by lazy { MainContext.inject<Console>() }

    /**
     * Will be called the first time a screen is shown
     */
    open fun firstShown() {}

    override fun show() {
        super.show()
        if (!isAlreadyShown) {
            isAlreadyShown = true
            firstShown()
        }
        MegaManagers.inputActionManager.subscribe(this)
        info { "Showing $name Screen" }
    }

    override fun hide() {
        super.hide()
        MegaManagers.inputActionManager.unsubscribe(this)
        info { "Hiding $name Screen" }
    }

    override fun pause() {
        super.pause()
        info { "Pausing $name Screen" }
    }

    override fun resume() {
        super.resume()
        info { "Resuming $name Screen" }
    }

    override fun dispose() {
        super.dispose()
        info { "Disposing $name Screen" }
    }

    override fun render(delta: Float) {
        viewport.apply()
        hudViewport.apply()
        actCrew(delta)
        drawCrew()
        updateCursor()
    }

    protected fun drawCrew() {
        backgroundCrew.render()
        crew.render()
        hudCrew.render()
    }

    protected fun actCrew(delta: Float) {
        backgroundCrew.act(delta)
        crew.act(delta)
        hudCrew.act(delta)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        info { "Resizing $name Screen, with width:$width and height:$height" }
        viewport.update(width, height)
        hudViewport.update(width, height)
    }

    fun setFocusOnAllActors(focus: Boolean) {
        setFocusOnActors(crew.members, focus)
        setFocusOnActors(backgroundCrew.members, focus)
    }

    fun setFocusOnActors(actors: List<BaseActor>, focus: Boolean) {
        actors.forEach { actor ->
            when (actor) {
                is Crew -> {
                    setFocusOnActors(actor.members, focus)
                }

                else -> {
                    actor.isFocused = focus
                }
            }
        }
    }

    fun shakeCamera(duration: Float = 0.4f, power: Float = 3.2f) {
        MegaManagers.screenManager.addGlobalAction(
            CameraShakeAction(camera, duration, power)
        )
    }

    fun addLetterboxes(top: Letterbox, bottom: Letterbox) {
        hudCrew.addMembers(top, bottom)
    }

    fun removeLetterBoxes() {
        hudCrew.removeAllOf<Letterbox>()
    }

    internal fun getMousePosition(): Vector2 {
        val pos = viewport.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
        return Vector2(pos.x, pos.y)
    }

    internal fun getClickablesUnderMouse(list: List<BaseActor> = crew.members.toList()): List<Clickable> {
        val mousePos = getMousePosition()
        return list
            .filter { it.hit(mousePos.x, mousePos.y, true) != null }
            .sortedByDescending { it.drawIndex }
            .filterIsInstance<Clickable>()
    }

    internal fun getMembersUnderMouse(list: ArrayList<BaseActor> = crew.members): List<BaseActor> {
        val mousePos = getMousePosition()
        return list.filter {
            it.hit(mousePos.x, mousePos.y, true) != null
        }.sortedByDescending { it.drawIndex }
    }

    private fun updateCursor() {
        if (getClickablesUnderMouse(hudCrew.members.toList()).isNotEmpty()) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand)
        } else {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow)
        }
    }

    /**
     * Do an action after a certain delay
     */
    internal fun withTimer(delay: Float, callback: () -> Unit) {
        MegaManagers.screenManager.addGlobalAction(Actions.delay(delay, Actions.run(callback)))
    }

    /**
     * Do an action after a certain delay
     */
    internal fun withTimer(delay: Float, callback: Action) {
        MegaManagers.screenManager.addGlobalAction(Actions.delay(delay, callback))
    }

    override fun toString(): String {
        return name
    }

}
