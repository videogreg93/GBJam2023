package com.odencave.i18n.entities.enemy.spawner

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.i18n.entities.enemy.Enemy
import gaia.Globals
import gaia.base.BaseActor

class EnemySpawner : BaseActor() {
    private val amountOfLanes = 9
    private val distanceBetweenLanes: Int
        get() = Globals.WORLD_HEIGHT.toInt() / amountOfLanes
    private val enemySequenceAction = Actions.sequence()

    private fun getSpawnPositionForLane(lane: Int): Vector2 {
        if (lane >= amountOfLanes || lane < 0 ) {
            Gdx.app.error("EnemySpawner", "Invalid lane value $lane")
        }
        val spawnY = (-Globals.WORLD_HEIGHT/2) + (lane * distanceBetweenLanes)
        val spawnX = Globals.WORLD_WIDTH/2
        return Vector2(spawnX, spawnY)
    }

    fun addEnemy(spawnConfigurations: List<SpawnConfiguration>, delayFromPrevious: Float) {
        enemySequenceAction.addAction(
            Actions.delay(delayFromPrevious, Actions.run {
                spawnConfigurations.forEach {
                    val spawnPosition = getSpawnPositionForLane(it.lane)
                    it.enemy.setPosition(spawnPosition.x, spawnPosition.y)
                    crew?.addMember(it.enemy)
                }
            })
        )
    }

    fun start() {
        addAction(enemySequenceAction)
    }
}
