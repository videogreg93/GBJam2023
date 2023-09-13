package gaia.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.I18NBundle
import gaia.generated.Nls
import gaia.managers.prefs.Prefs
import ktx.i18n.get
import java.util.*

class TextBoy : MegaManagers.Manager {
    lateinit var bundle: I18NBundle

    override fun init() {
        bundle = I18NBundle.createBundle(Gdx.files.internal(FILE_NAME), Prefs.getPreferredLocale())
    }

    fun text(key: Nls): String {
        return bundle[key]
    }

    fun text(key: Nls, arg1: Any): String {
        return bundle[key, arg1]
    }

    fun text(key: Nls, arg1: Any, arg2: Any): String {
        return bundle[key, arg1, arg2]
    }

    fun changeLocale(locale: Locale) {
        bundle = I18NBundle.createBundle(Gdx.files.internal(FILE_NAME), locale)
    }

    companion object {
        private const val FILE_NAME = "i18n/nls"
    }
}
