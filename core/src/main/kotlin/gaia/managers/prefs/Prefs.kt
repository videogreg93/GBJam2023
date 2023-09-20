package gaia.managers.prefs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Preferences
import gaia.managers.MegaManagers
import java.util.*

/**
 * Modifying the prefs files at runtime will not reflect reality. Prefs from file are loaded the first time they are accessed,
 * then the correct value is stored in memory.
 */
object Prefs : MegaManagers.Manager {
    const val folder = "Zenith"
    private const val prefsName = "prefs"
    private const val statsName = "${folder}/Stats"
    private const val PREFERRED_LOCALE = "locale"


    private val prefs: Preferences by lazy { Gdx.app.getPreferences(prefsName) }
    private val stats: Preferences by lazy { Gdx.app.getPreferences(statsName) }

    override fun init() {

    }

    fun getInt(key: String): Int {
        return prefs.getInteger(key, 0)
    }

    fun setInt(value: Int, key: String) = prefs.putInteger(key, value).flush()

    fun getPreferredLocale(): Locale {
        val locale = prefs.getString(PREFERRED_LOCALE)
        return if (locale.isNullOrBlank()) {
            Locale.getDefault()
        } else {
            Locale.forLanguageTag(locale)
        }
    }


    fun resetStats() {
        stats.clear()
        stats.flush()
    }

    fun resetPrefs() {
        prefs.clear()
        prefs.flush()
    }

    private fun getAllKeyValues(file: Preferences): List<String> {
        return file.get().map {
            "${it.key}: ${it.value}"
        }
    }

    fun getAllStats(): List<String> {
        return getAllKeyValues(stats)
    }

}
