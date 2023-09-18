package gaia

import com.badlogic.gdx.graphics.Color
import com.odencave.i18n.models.Palette


object Globals {
    const val WORLD_WIDTH = 160f
    const val WORLD_HEIGHT = 144f
    var currentBackgroundColor = Palette.allPalettes.first().color4
    var gameSpeed = 1f
    var godMode = false

    var world2Unlocked = false
    var world3Unlocked = false
    var world4Unlocked = false
}
