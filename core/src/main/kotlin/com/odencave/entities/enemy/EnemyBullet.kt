package com.odencave.entities.enemy

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.assets.Assets
import com.odencave.entities.Entity
import gaia.base.Crew
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get

class EnemyBullet(val direction: Vector2): Entity(bulletAsset.get()) {

    override fun act(delta: Float) {
        super.act(delta)
        x += (direction.x * DEFAULT_SPEED * delta)
        y += (direction.y * DEFAULT_SPEED * delta)
        if (isCompletelyOutOfBounds()) {
            removeFromCrew()
        }
    }

    override fun onAddedToCrew(crew: Crew) {
        super.onAddedToCrew(crew)
        addAction(
            Actions.forever(
                Actions.rotateBy(10f)
            )
        )
    }

    companion object {
        private const val DEFAULT_SPEED = 80f

        @Asset
        private val bulletAsset = AssetDescriptor(Assets.Enemy.bullet, Texture::class.java)
    }
}
