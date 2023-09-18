package com.odencave.i18n.entities.enemy.spawner

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.entities.enemy.spawner.SpawnerAction
import gaia.Globals
import gaia.base.BaseActor
import gaia.ui.utils.skip
import kotlin.reflect.jvm.isAccessible

class EnemySpawner : BaseActor() {
    private val amountOfLanes = LANE_COUNT
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

    fun skipCurrentWave() {
        enemySequenceAction.skip()
    }

    fun addEnemy(configs: List<SpawnConfiguration>, delayFromPrevious: Float = 0f) {
        val actionToDo = Actions.parallel().apply {
            configs.forEach {
                addAction(Actions.run {
                    val spawnPosition = getSpawnPositionForLane(it.lane)
                    it.enemy.setPosition(spawnPosition.x, spawnPosition.y)
                    crew?.addMember(it.enemy)
                })
            }
        }
        enemySequenceAction.addAction(
            Actions.delay(delayFromPrevious, actionToDo)
        )
    }

    fun wait(delay: Float) {
        enemySequenceAction.addAction(Actions.delay(delay))
    }

    fun start() {
        addAction(enemySequenceAction)
    }

    companion object {
        const val LANE_COUNT = 9
    }
}
