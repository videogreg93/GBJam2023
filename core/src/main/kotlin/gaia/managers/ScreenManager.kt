package gaia.managers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Action
import gaia.managers.assets.AssetManager.Companion.get
import gaia.ui.BasicScreen

class ScreenManager {
    var screenListener: ChangeScreenListener? = null
    var currentTransitionTexture: Texture? = null
    val batch: SpriteBatch by lazy { MegaManagers.currentContext.inject<SpriteBatch>() }
    var canTransition = true

    fun changeScreen(screen: BasicScreen) {
        screenListener?.onScreenChange(screen)
    }

    /**
     * Effectively destroys backstack. Might want to automatically do this when going to the main menu.
     */
    fun removeAllScreensButMainMenu() {
        screenListener?.removeAllScreensButMainMenu()
    }

    fun <T : BasicScreen> returnToScreen(clazz: Class<T>, disposeCurrent: Boolean = false) {
        screenListener?.returnToScreen(clazz, disposeCurrent)
    }

    fun getCurrentScreen(): BasicScreen? {
        return screenListener?.getCurrentScreen()
    }

    fun fadeTo(
        screen: BasicScreen,
        color: Color = Color.BLACK,
        speedMultiplier: Float = 1f,
        onFinish: (() -> Unit)? = null
    ) {
        screenListener?.fadeTo(screen, color, speedMultiplier, onFinish)
    }

    fun addGlobalAction(action: Action) {
        screenListener?.addGlobalAction(action)
    }

    fun removeGlobalAction(action: Action) {
        screenListener?.removeGlobalAction(action)
    }

    fun addNewScreen(screen: BasicScreen) {
        screenListener?.addNewScreen(screen)
    }

    // Transitions
//    fun transitionToScreen(
//        transition: TRANSITION = TRANSITION.CONE,
//        duration: Float = 1f,
//        flipFlop: Boolean = false,
//        screen: BasicScreen
//    ) {
//        if (!canTransition) return
//        screenListener?.addNewScreen(screen)
//        transitionToScreen(transition, duration, flipFlop, screen::class.java)
//    }

    /* fun <T : BasicScreen> transitionToScreen(transition: TRANSITION = TRANSITION.CONE, duration: Float = 1f, flipFlop: Boolean = false, screen: Class<T>) {
         if (!canTransition) return
         canTransition = false
         MegaManagers.inputActionManager.inputEnabled = false
 //        val transitionShader = Shaders.transitionShader
 //        batch.shader = transitionShader
         currentTransitionTexture = transition.toTexture()
         val finalAction = Actions.sequence()
         val transitionAction = FloatLerpAction(0f, 1f) {
             transitionShader.use { shader ->
                 val inverse = if (!flipFlop && it >= 0.5f) 1 else 0
                 shader.setUniformf("cutoff", (it/2 * 1000).toInt()/1000f)
                 shader.setUniformi("inverse", inverse)
                 shader.setUniformf("u_resolution", Vector2(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))
                 Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE1)
                 currentTransitionTexture?.bind(1)
                 shader.setUniformi("transition", 1)

                 Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE0)
                 shader.setUniformi("u_texture", 0)
                 shader.end()
             }
         }
         transitionAction.duration = duration/2f
         transitionAction.interpolation = Interpolation.linear
         finalAction.addAction(transitionAction)
         finalAction.addAction(Actions.run {
             batch.shader = null
             canTransition = true
             MegaManagers.inputActionManager.inputEnabled = true
             info { "Can Transition now" }
         })
         addGlobalAction(finalAction)
         addGlobalAction(Actions.delay(duration * 0.25f, Actions.run {
             screenListener?.returnToScreen(screen, false)
         }))
     }*/

//    fun TRANSITION.toTexture(): Texture {
//        return when (this) {
//            TRANSITION.LEFT_TO_RIGHT -> leftToRightTransitionAsset.get()
//            TRANSITION.CLOSE -> closeAnimationAsset.get()
//            TRANSITION.CONE -> coneTransitionAsset.get()
//            TRANSITION.OUT -> outTransitionAsset.get()
//            TRANSITION.DOWN_TO_UP -> downToUpTransitionAsset.get()
//        }
//    }

    enum class TRANSITION {
        LEFT_TO_RIGHT,
        CLOSE,
        CONE,
        OUT,
        DOWN_TO_UP
    }

    companion object {
//        @Asset(Asset.TYPE.MAIN_MENU)
//        val leftToRightTransitionAsset = AssetDescriptor(Assets.Shaders.leftToRight2, Texture::class.java)
//
//        @Asset(Asset.TYPE.MAIN_MENU)
//        val closeAnimationAsset = AssetDescriptor(Assets.Shaders.close, Texture::class.java)
//
//        @Asset(Asset.TYPE.MAIN_MENU)
//        val coneTransitionAsset = AssetDescriptor(Assets.Shaders.cone, Texture::class.java)
//
//        @Asset(Asset.TYPE.MAIN_MENU)
//        val outTransitionAsset = AssetDescriptor(Assets.Shaders.out, Texture::class.java)
//
//        @Asset(Asset.TYPE.MAIN_MENU)
//        val downToUpTransitionAsset = AssetDescriptor(Assets.Transitions.downToUp, Texture::class.java)
    }

    interface ChangeScreenListener {
        fun getCurrentScreen(): BasicScreen?
        fun onScreenChange(screen: BasicScreen)
        fun fadeTo(screen: BasicScreen, color: Color = Color.BLACK, speedMultiplier: Float, onFinish: (() -> Unit)?)
        fun <Type : BasicScreen> returnToScreen(screenClass: Class<Type>, disposeCurrent: Boolean)
        fun addNewScreen(screen: BasicScreen)
        fun addGlobalAction(action: Action)
        fun removeGlobalAction(action: Action)
        fun showLetterbox()
        fun hideLetterbox()
        fun removeAllScreensButMainMenu()
    }

}
