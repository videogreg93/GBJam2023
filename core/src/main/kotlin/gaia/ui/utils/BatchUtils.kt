package gaia.ui.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch

fun Batch.withColor(color: Color, callback: (Batch) -> Unit) {
    val batchColor = Color(this.color)
    this.color = color
    callback.invoke(this)
    this.color = batchColor
}
