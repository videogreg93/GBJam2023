package com.gregory

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.FitViewport
import com.odencave.ScoreManager
import com.odencave.i18n.screens.LoadingScreen
import com.odencave.i18n.screens.MainScreen
import gaia.Globals
import gaia.Globals.WORLD_HEIGHT
import gaia.Globals.WORLD_WIDTH
import gaia.base.Crew
import gaia.managers.MegaManagers
import gaia.managers.ModalManager
import gaia.managers.ScreenManager
import gaia.ui.BasicScreen
import gaia.ui.Letterbox
import gaia.ui.Modal
import gaia.utils.ActorAction
import gaia.utils.addAsInput
import ktx.app.KtxGame
import ktx.app.KtxInputAdapter
import ktx.app.clearScreen
import ktx.async.KtxAsync
import kotlin.system.exitProcess

class MainApplication : KtxGame<BasicScreen>(),
    ScreenManager.ChangeScreenListener, ModalManager.ModalListener, KtxInputAdapter {
    var isFading = false
    var fadeDirection = true
    var fadeToScreen: BasicScreen? = null
    var alpha = 0f
    var fadeColor = Color.BLACK
    val fadeShapeRenderer by lazy { ShapeRenderer() }
    var fadeMultiplier = 1f
    var onFinishFade: (() -> Unit)? = null

    // Modals
    val modals = ArrayList<Modal>()
    val modalShapeRenderer by lazy { ShapeRenderer() }
    val modalBackgroundColor = Color.BLACK
    val modalAlpha = 0.6f
    override val currentModal: Modal?
        get() = modals.lastOrNull()

    // Dev tools

    // Persistent objects and things to display over all screens
    lateinit var overlayCrew: Crew
    val overlayCamera by lazy { OrthographicCamera() }
    val overlayViewport by lazy { FitViewport(WORLD_WIDTH, WORLD_HEIGHT, overlayCamera) }


    init {
        // No idea what this does but stops game from crashing on macbooks sometimes
        ShaderProgram.pedantic = false
    }

    override fun onScreenChange(screen: BasicScreen) {
        removeScreen(screen.javaClass)?.dispose()
        addScreen(screen)
        setScreen(screen.javaClass)
    }

    override fun removeAllScreensButMainMenu() {
        // do nothing
    }

    override fun fadeTo(screen: BasicScreen, color: Color, speedMultiplier: Float, onFinish: (() -> Unit)?) {
        isFading = true
        alpha = 0f
        fadeToScreen = screen
        fadeColor = color
        fadeMultiplier = speedMultiplier
        onFinishFade = onFinish
    }

    override fun <Type : BasicScreen> returnToScreen(screenClass: Class<Type>, disposeCurrent: Boolean) {
        if (disposeCurrent) {
            getCurrentScreen()?.let {
                removeScreen(it::class.java)?.dispose()
            }
        }
        if (!containsScreen(screenClass)) {
            ktx.log.error { "Creating screen of type $screenClass" }
            addNewScreen(screenClass.newInstance())
        }
        setScreen(screenClass)
    }

    override fun getCurrentScreen(): BasicScreen {
        return shownScreen
    }

    override fun addNewScreen(screen: BasicScreen) {
        addScreen(screen)
    }

    val managers = MegaManagers

    override fun create() {
        super.create()
        managers.init(emptyArray())
        // Game specific managers
        managers.registerManager(ScoreManager())
        overlayCrew = Crew(MegaManagers.currentContext.inject(), overlayCamera)
        KtxAsync.initiate()
        MegaManagers.screenManager.screenListener = this
        MegaManagers.modalManager.setModalListener(this)
        addScreen(LoadingScreen())
        setScreen<LoadingScreen>()
//        console = MainContext.inject()
//        console.setPositionPercent(30f, 30f)
        addAsInput()
        MegaManagers.inputActionManager.loadKeyboardMappings()
//        val cursor = Gdx.graphics.newCursor(Pixmap(Gdx.files.internal(Assets.Generic.cursor)), 0, 31)
//        Gdx.graphics.setCursor(cursor) TODO
        //Gdx.input.isCursorCatched = true // TODO remove this if we want to see cursor
    }

    // TODO should we stop render/act when fading?
    override fun render() {
        val deltaTime = if (modals.lastOrNull()?.pauseMainScreen == true) 0f else Gdx.graphics.deltaTime
        val bgColor = Globals.currentBackgroundColor
        clearScreen(bgColor.x, bgColor.y, bgColor.z, 1f)
        currentScreen.render(deltaTime * Globals.gameSpeed)
        handleModal()
//        MegaManagers.devTools?.render()
        // TODO CLEANER WAY OF DOING PERIODIC WORK
        MegaManagers.eventManager.cleanup()
        handleFade()
        overlayViewport.apply()
        overlayCrew.act(deltaTime)
        overlayCrew.draw(MegaManagers.currentContext.inject<SpriteBatch>(), 1f)
//        console.draw()
    }

    private fun handleModal() {
        if (modals.isNotEmpty()) {
            if (modals.last().showOverlay) {
                Gdx.gl.glEnable(GL20.GL_BLEND)
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
                modalShapeRenderer.projectionMatrix = overlayCamera.combined
                modalShapeRenderer.transformMatrix = overlayCrew.batch.transformMatrix
                modalShapeRenderer.setColor(
                    modalBackgroundColor.r,
                    modalBackgroundColor.g,
                    modalBackgroundColor.b,
                    modalAlpha
                )
                modalShapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
                modalShapeRenderer.rect(
                    -Globals.WORLD_WIDTH / 2f,
                    -Globals.WORLD_HEIGHT / 2f,
                    Globals.WORLD_WIDTH,
                    Globals.WORLD_HEIGHT
                )
                modalShapeRenderer.end()
                Gdx.gl.glDisable(GL20.GL_BLEND)
            }
            modals.last().render(Gdx.graphics.deltaTime)
        }
    }

    private fun handleFade() {
        if (isFading) {
            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
            fadeShapeRenderer.setColor(fadeColor.r, fadeColor.g, fadeColor.b, alpha)
            fadeShapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            fadeShapeRenderer.rect(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            fadeShapeRenderer.end()
            Gdx.gl.glDisable(GL20.GL_BLEND)

            alpha += if (fadeDirection) Gdx.graphics.deltaTime * fadeMultiplier else -Gdx.graphics.deltaTime * fadeMultiplier
            if (alpha >= 1) {
                fadeDirection = !fadeDirection
                fadeToScreen?.let {
                    onScreenChange(it)
                    fadeToScreen = null
                    alpha = 0.99f
                }
            } else if (alpha < 0) {
                fadeDirection = true
                alpha = 0f
                isFading = false
                fadeToScreen = null
                onFinishFade?.invoke()
            }
        }
    }

    override fun dispose() {
        MegaManagers.dispose()
        super.dispose()
        // TODO workaround because segment analytics keeps the game running in background otherwise.
        exitProcess(0)
    }

    override fun <Type : BasicScreen> addScreen(type: Class<Type>, screen: Type) {
        screens.put(screen.javaClass, screen)
    }

    override fun addModal(modal: Modal) {
        modals.add(modal)
        modal.resize(Gdx.graphics.width, Gdx.graphics.height)
        modal.show()
    }

    override fun removeModal(modal: Modal) {
        modal.hide()
        modals.remove(modal)
    }

    override fun addGlobalAction(action: Action) {
        overlayCrew.addAction(action)
    }

    override fun removeGlobalAction(action: Action) {
        overlayCrew.removeAction(action)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        modals.forEach { it.resize(width, height) }
        overlayViewport.update(width, height)
    }

    override fun showLetterbox() {
        getCurrentScreen()?.addLetterboxes(topLetterBox, bottomLetterBox)
        topLetterBox.addAction(
            Actions.moveBy(0f, -topLetterBox.height, 1f, Interpolation.fastSlow)
        )
        bottomLetterBox.addAction(
            Actions.moveBy(0f, topLetterBox.height, 1f, Interpolation.fastSlow)
        )
    }

    override fun hideLetterbox() {
        topLetterBox.addAction(
            Actions.sequence(
                Actions.moveBy(0f, topLetterBox.height, 1f, Interpolation.fastSlow),
                ActorAction.removeFromCrew()
            )
        )
        bottomLetterBox.addAction(
            Actions.sequence(
                Actions.moveBy(0f, -topLetterBox.height, 1f, Interpolation.fastSlow),
                ActorAction.removeFromCrew()
            )
        )
    }

    override fun keyDown(keycode: Int): Boolean {
        // Check for alt + enter for toggling fullscreen
        if ((keycode == Input.Keys.ENTER && Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) ||
            keycode == Input.Keys.ALT_LEFT && Gdx.input.isKeyPressed(Input.Keys.ENTER)
        ) {
            val isFullscreen = Gdx.graphics.isFullscreen
            if (isFullscreen) {
                Gdx.graphics.setWindowedMode(1280, 720)
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.displayModes.find { it.width == 1920 })
            }
            //MegaManagers.prefs.setFullScreen(!isFullscreen)
            return true
        }
        return false
    }

    companion object {
        val topLetterBox by lazy {
            Letterbox().apply {
                center()
                y = Globals.WORLD_HEIGHT / 2f
            }
        }
        val bottomLetterBox by lazy {
            Letterbox().apply {
                center()
                y = -Globals.WORLD_HEIGHT / 2f - height
            }
        }
    }

}
