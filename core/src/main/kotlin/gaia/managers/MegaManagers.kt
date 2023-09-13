package gaia.managers

import com.odencave.i18n.gaia.ui.shaders.Shaders
import gaia.managers.assets.AssetManager
import gaia.managers.context.MainContext
import gaia.managers.fonts.FontManager
import gaia.managers.input.InputActionManager
import gaia.managers.prefs.Prefs
import ktx.inject.Context

object MegaManagers {
    val randomManager: RandomManager = RandomManager()
    val assetManager = AssetManager()
    val inputActionManager = InputActionManager()
    val screenManager = ScreenManager()
    val modalManager = ModalManager()
    val fontManager = FontManager()
    val soundManager = SoundManager()
    val prefs by lazy { Prefs }
    val eventManager = EventManager()
    val textBoy = TextBoy()
    lateinit var currentContext: Context

    fun init(args: Array<String>) {
        MainContext.register()
        currentContext = MainContext.context
        prefs.init()
        assetManager.init()
        inputActionManager.init()
        fontManager.init()
        soundManager.init()
        textBoy.init()
        Shaders.initShaders()
    }

    fun dispose() {
        soundManager.dispose()
    }

    interface Manager {
        fun init()
    }
}
