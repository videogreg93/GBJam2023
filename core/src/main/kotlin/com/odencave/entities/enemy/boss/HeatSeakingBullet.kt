package com.odencave.entities.enemy.boss

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.SFX
import com.odencave.entities.Entity
import com.odencave.entities.enemy.EnemyBullet
import com.odencave.entities.player.Player
import gaia.base.Crew
import gaia.managers.MegaManagers
import gaia.managers.assets.AssetManager.Companion.get
import ktx.math.minus

class HeatSeakingBullet(var targetWaitPosition: Vector2, val delayUntilSeek: Float = 1f): Entity(EnemyBullet.bulletAsset.get()) {
    val speed1 = 90f
    val speed2 = 100f
    var behavior = Behavior.SeekingWaitPosition
    lateinit var initialPosition: Vector2

    override fun onAddedToCrew(crew: Crew) {
        super.onAddedToCrew(crew)
        initialPosition = Vector2(x,y)
        addAction(
            Actions.forever(
                Actions.rotateBy(10f)
            )
        )
    }

    override fun act(delta: Float) {
        super.act(delta)
        val myPos = Vector2(x,y)
        when (behavior) {
            Behavior.SeekingWaitPosition -> {
                val direction = (targetWaitPosition - initialPosition).nor()
                x += (direction.x * speed1 * delta)
                y += (direction.y * speed1 * delta)
                if (myPos.dst(targetWaitPosition) < 2f) {
                    behavior = Behavior.Waiting
                    val playerPos = crew?.getAllOf<Player>()?.firstOrNull()?.pos() ?: error("WEFNMEW")
                    addAction(Actions.delay(delayUntilSeek, Actions.run {
                        targetWaitPosition = (playerPos - myPos).nor()
                        behavior = Behavior.SeekingPlayer
                        MegaManagers.soundManager.playSFXRandomPitch(SFX.bossShoot.get())
                    }))
                }
            }
            Behavior.Waiting -> {
                // Do nothing
            }
            Behavior.SeekingPlayer -> {
                x += (targetWaitPosition.x * speed2 * delta)
                y += (targetWaitPosition.y * speed2 * delta)
                if (isCompletelyOutOfBounds()) {
                    removeFromCrew()
                }
            }
        }
    }

    enum class Behavior {
        SeekingWaitPosition, Waiting, SeekingPlayer
    }
}
