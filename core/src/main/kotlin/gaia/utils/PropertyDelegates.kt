package gaia.utils

import com.odencave.i18n.screens.MainScreen
import gaia.managers.MegaManagers
import gaia.managers.prefs.Prefs
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class IntProperty(): ReadWriteProperty<Any, Int> {
    val prefs: Prefs
        get() = MegaManagers.prefs

    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        val key = property.name
        return prefs.getInt(key)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        val key = property.name
        prefs.setInt(value, key)
    }
}
