package gaia.ui


import gaia.Globals.WORLD_WIDTH
import gaia.base.BaseActor

class Letterbox : BaseActor() {

    init {
        width = WORLD_WIDTH
        height = 138f // 2.39:1 aspect ratio
        drawIndex = -1000
    }
}
