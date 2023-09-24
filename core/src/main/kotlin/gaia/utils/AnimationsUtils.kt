package gaia.utils

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.collections.toGdxArray


fun createAnimation(
    sheetName: String,
    rows: Int,
    columns: Int,
    animationSpeed: Float,
    loop: Boolean = false
): Animation<TextureRegion> {
    return createAnimation(Texture(sheetName), rows, columns, animationSpeed, loop)
}

fun createAnimation(
    runSheet: Texture,
    rows: Int,
    columns: Int,
    animationSpeed: Float,
    loop: Boolean = false
): Animation<TextureRegion> {
    val temp = TextureRegion.split(runSheet, runSheet.width / columns, runSheet.height / rows)
    val runFrames = ArrayList<TextureRegion>()
    for (i in 0 until rows) {
        for (j in 0 until columns) {
            runFrames.add(temp[i][j])
        }
    }

    return Animation(animationSpeed, runFrames.toGdxArray()).apply {
        if (loop) playMode = Animation.PlayMode.LOOP
    }
}

fun Animation<TextureRegion>.height(): Int {
    return keyFrames[0].regionHeight
}

fun Animation<TextureRegion>.width(): Int {
    return keyFrames[0].regionWidth
}
