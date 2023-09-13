package gaia.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.Json
import gaia.managers.prefs.Prefs
import ktx.log.info
import kotlin.properties.Delegates
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties

object GlobalFlags {
    private val prefs by lazy { Gdx.app.getPreferences("${Prefs.folder}/prefs") }

    /**
     * @return true if prefs already existed, false otherwise
     */
    fun setExistingBooleanPref(name: String, value: Boolean): Boolean {
        return if (prefs.contains(name)) {
            prefs.putBoolean(name, value)
            prefs.flush()
            true
        } else {
            false
        }
    }


    fun booleanPrefs(file: Preferences, name: String, defaultValue: Boolean) =
        object : ObservableProperty<Boolean>(file.getBoolean(name, defaultValue)) {
            override fun afterChange(property: KProperty<*>, oldValue: Boolean, newValue: Boolean) {
                info { "$name: $newValue" }
                file.putBoolean(name, newValue)
                file.flush()
            }

            override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
                return file.getBoolean(name, defaultValue)
            }
        }

    fun intPrefs(file: Preferences, name: String, defaultValue: Int) =
        Delegates.observable(file.getInteger(name, defaultValue)) { _, _, newValue ->
            info { "$name: $newValue" }
            file.putInteger(name, newValue)
            file.flush()
        }

    fun intArrayPrefs(file: Preferences, name: String) =
        Delegates.observable(
            Json().fromJson(
                arrayListOf<Int>()::class.java,
                file.getString(name, "[]")
            )
        ) { _, _, newValue ->
            info { "$name: $newValue" }
            file.putString(name, Json().toJson(newValue))
            file.flush()
        }

    fun getAllFields(): List<String> {
        return GlobalFlags::class.declaredMemberProperties
            .filterNot { it.name == "prefs" }
            .map {
                "${it.name} : ${it.get(this)}"
            }
    }
}
