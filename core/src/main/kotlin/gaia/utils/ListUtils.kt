package gaia.utils

fun List<*>.wrappingCursor(value: Int): Int = when {
    isEmpty() -> {
        0
    }
    value < 0 -> {
        value + size
    }
    else -> {
        value % size
    }
}

inline fun <T> Iterable<T>.sumByFloat(selector: (T) -> Float): Float {
    var sum: Float = 0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

fun <T> T.toArraylist(): ArrayList<T> {
    return ArrayList(listOf(this))
}
