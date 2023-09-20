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
import com.odencave.i18n.entities.enemy.spawner.EnemySpawner
import gaia.Globals
import gaia.managers.MegaManagers
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get

open class Enemy(texture: Texture = johnAsset.get()) : Entity(texture) {

    var scoreValue = 10
    var currentMoveSpeed = DEFAULT_ENEMY_MOVE_SPEED

    fun moveStraight(speed: Float = DEFAULT_ENEMY_MOVE_SPEED) {
        currentMoveSpeed = speed
        addMovementAction(MoveStraightAction())
    }

    fun moveUpALane(delay: Float = 1f) = moveLanes(delay, 1)

    fun moveDownALane(delay: Float = 1f) = moveLanes(delay, -1)

    fun moveLanes(delay: Float = 1f, laneCount: Int) {
        val distanceBetweenLanes = Globals.WORLD_HEIGHT / EnemySpawner.LANE_COUNT
        addAction(
            Actions.delay(delay, Actions.moveBy(
                0f,
                distanceBetweenLanes * laneCount,
                0.1f
            ))
        )
    }

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
        const val FASTER_ENEMY_MOVE_SPEED = 65f

        fun moveStraightEnemy(speed: Float = DEFAULT_ENEMY_MOVE_SPEED) = Enemy().apply {
            moveStraight(speed)
        }

        @Asset
        private val johnAsset = AssetDescriptor(Assets.Enemy.john, Texture::class.java)
    }
}
