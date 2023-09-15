package com.odencave.entities

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import gaia.Globals.WORLD_HEIGHT
import gaia.Globals.WORLD_WIDTH
import gaia.base.BaseActor
import gaia.managers.assets.AssetManager.Companion.get

open class Entity(texture: Texture?) : BaseActor(texture) {

    open val AABB: Rectangle
        get() = Rectangle(x, y, width, height)

    constructor(asset: AssetDescriptor<Texture>) : this(asset.get())

    fun isCompletelyOutOfBounds(): Boolean {
        return (x < (-WORLD_WIDTH / 2) - width || x > WORLD_WIDTH / 2 || y < (-WORLD_HEIGHT) - height || y > WORLD_HEIGHT / 2)
    }

    fun isPartlyOutOfBounds(): Boolean {
        return (x < (-WORLD_WIDTH / 2) || x + width > WORLD_WIDTH / 2 || y < (-WORLD_HEIGHT / 2) || y + height > WORLD_HEIGHT / 2)
    }

    fun isCollidingWith(other: Entity): Boolean {
        return AABB.overlaps(other.AABB)
    }

    open fun onCollision(other: Entity) {

    }
}
