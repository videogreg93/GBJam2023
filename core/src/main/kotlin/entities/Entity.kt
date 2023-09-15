package com.odencave.i18n.entities

import com.badlogic.gdx.graphics.Texture
import gaia.Globals.WORLD_HEIGHT
import gaia.Globals.WORLD_WIDTH
import gaia.base.BaseActor

open class Entity(texture: Texture?): BaseActor(texture) {

    fun isCompletelyOutOfBounds(): Boolean {
        return (x < (-WORLD_WIDTH / 2) - width || x > WORLD_WIDTH / 2 || y < (-WORLD_HEIGHT) - height || y > WORLD_HEIGHT / 2)
    }

    fun isPartlyOutOfBounds(): Boolean {
        return (x < (-WORLD_WIDTH / 2) || x + width > WORLD_WIDTH / 2 || y < (-WORLD_HEIGHT/2) || y + height > WORLD_HEIGHT / 2)
    }
}
