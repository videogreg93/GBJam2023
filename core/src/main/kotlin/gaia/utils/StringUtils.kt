package gaia.utils

import com.badlogic.gdx.graphics.g2d.BitmapFont
import gaia.generated.Nls
import gaia.managers.MegaManagers
import java.util.*

/**
 * Returns a string with new line characters every couple of words
 */
fun String.wrapped(font: BitmapFont, maxWidth: Int): String {
    var result = ""
    var tester = ""
    val words = this.split(" ")
    words.forEachIndexed { index, word ->
        tester += "$word "
        if (index == words.size - 1) {
            tester = tester.dropLast(0)
        }
        if (font.getWidth(tester) > maxWidth) {
            result += "\n$word "
            tester = result
        } else {
            result = tester
        }
    }
    return result
}

// Turns a string into a colored string for fonts using markup enabled
fun String.grey(): String {
    return "[GRAY]$this[]"
}

fun String.white(): String {
    return "[WHITE]$this[]"
}

fun String.green(): String {
    return "[GREEN]$this[]"
}

fun String.olive(): String {
    return "[#588157]$this[]"
}

fun String.red(): String {
    return "[RED]$this[]"
}

fun String.black(): String {
    return "[BLACK]$this[]"
}

val colorRegex = Regex("\\[[a-zA-Z]+\\][a-zA-Z]*\\[\\]")

fun String.removeColor(): String {
    if (!colorRegex.containsMatchIn(this)) return this
    return this.split("]")[1].dropLast(1)
}

fun String.cap(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

val Nls.text: String
    get() = MegaManagers.textBoy.text(this)
