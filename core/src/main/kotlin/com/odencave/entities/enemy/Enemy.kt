package com.odencave.entities.enemy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.assets.Assets
import com.odencave.entities.Entity
import com.odencave.events.EnemyDestroyedEvent
import com.odencave.i18n.entities.enemy.MoveStraightAction
import gaia.managers.MegaManagers
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get

class Enemy : Entity(johnAsset.get()) {
    var scoreValue = 10
    var currentMoveSpeed = DEFAULT_ENEMY_MOVE_SPEED

    fun moveStraight() = addMovementAction(MoveStraightAction())

    private fun addMovementAction(action: Action) = addAction(Actions.forever(action))

    fun destroy() {
        removeFromCrew()
        MegaManagers.eventManager.sendEvent(EnemyDestroyedEvent(this))
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (isCompletelyOutOfBounds()) {
            Gdx.app.log("Enemy", "Removed because out of bounds")
            removeFromCrew()
        }
    }

    // TODO remove from crew when out of bounds.

    companion object {
        const val DEFAULT_ENEMY_MOVE_SPEED = 45f

        fun moveStraightEnemy() = Enemy().apply {
            moveStraight()
        }

        @Asset
        private val johnAsset = AssetDescriptor(Assets.Enemy.john, Texture::class.java)
    }
}
