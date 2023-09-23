package com.odencave.i18n.entities.enemy.spawner

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.entities.enemy.Boss
import com.odencave.entities.enemy.Enemy
import com.odencave.entities.enemy.spawner.EndLevelEvent
import gaia.Globals
import gaia.base.BaseActor
import gaia.managers.MegaManagers
import gaia.ui.utils.skip

class EnemySpawner : BaseActor() {
    private val amountOfLanes = LANE_COUNT
    private val distanceBetweenLanes: Int
        get() = Globals.WORLD_HEIGHT.toInt() / amountOfLanes
    private val enemySequenceAction = Actions.sequence()

    private fun getSpawnPositionForLane(lane: Int): Vector2 {
        if (lane >= amountOfLanes || lane < 0) {
            Gdx.app.error("EnemySpawner", "Invalid lane value $lane")
        }
        val spawnY = (-Globals.WORLD_HEIGHT / 2) + (lane * distanceBetweenLanes)
        val spawnX = Globals.WORLD_WIDTH / 2
        return Vector2(spawnX, spawnY)
    }

    fun skipCurrentWave() {
        enemySequenceAction.skip()
    }

    fun addBoss() {
        val action = Actions.run {
            val boss = Boss().apply {
                center()
                x = Globals.WORLD_WIDTH/2f
            }
            crew?.addMember(boss)
        }
        enemySequenceAction.addAction(action)
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

    fun addEnemyAction(config: SpawnConfiguration): Action {
        return Actions.run {
            val spawnPosition = getSpawnPositionForLane(config.lane)
            config.enemy.setPosition(spawnPosition.x, spawnPosition.y)
            crew?.addMember(config.enemy)
        }
    }

    fun wave(i: Int, lambda: (() -> Unit)) {
        if (i >= Globals.startAtWave) {
            enemySequenceAction.addAction(Actions.run {
                Gdx.app.log("WAVE", "Starting wave $i")
            })
            lambda.invoke()
        }
    }

    fun addActionToSequence(action: Action) = enemySequenceAction.addAction(action)

    fun finishLevel() {
        addActionToSequence(
            Actions.run {
                MegaManagers.eventManager.sendEvent(EndLevelEvent())
            }
        )
    }

    fun addJohn(lane: Int, delay: Float = 0f) {
        addEnemy(
            listOf(
                SpawnConfiguration(
                    Enemy.moveStraightEnemy(),
                    lane
                )
            ),
            delay
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
