package gaia

import com.odencave.i18n.models.Palette
import gaia.utils.wrappingCursor


object Globals {
    const val WORLD_WIDTH = 160f
    const val WORLD_HEIGHT = 144f
    var currentBackgroundColor = Palette.allPalettes.first().color4
    var selectedPaletteIndex: Int = 0
        set(value) {
            field = Palette.allPalettes.wrappingCursor(value)
        }
    val selectedPalette: Palette
        get() = Palette.allPalettes[selectedPaletteIndex]
    var gameSpeed = 1f
    var godMode = false
    var startAtWave: Int = 1
    var skipIntro: Boolean = false

    var world2Unlocked = false
    var world3Unlocked = false
    var world4Unlocked = false
}
