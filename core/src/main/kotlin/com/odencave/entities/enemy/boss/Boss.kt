package com.odencave.entities.enemy.boss

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.odencave.assets.Assets
import com.odencave.entities.enemy.Enemy
import com.odencave.entities.enemy.EnemyBullet
import com.odencave.entities.player.Player
import gaia.Globals
import gaia.base.Crew
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get
import gaia.utils.createAnimation
import ktx.actors.plus
import ktx.math.minus
import kotlin.math.sin

class Boss : Enemy(idleTexture.get()) {

    private var canFlicker = true

    val maxHealth = 100
    var currentHealth = maxHealth
        set(value) {
            field = value.coerceAtLeast(0)
        }
    var startingPosition: Vector2 = Vector2(15f, -48.0f)

    init {
        setPosition(1000f, 1000f)
        updateSprite()
        shouldDraw = false
        currentAnimation = idleAnimation
    }

    override fun onAddedToCrew(crew: Crew) {
        super.onAddedToCrew(crew)
        val sequence = Actions.sequence().apply {
            addAction(Actions.run { shouldDraw = true })
            addAction(Actions.moveBy(-(width / 2f + 20), 0f, 4f, Interpolation.fastSlow))
            addAction(Actions.delay(0.5f))
            addAction(Actions.run {
                startingPosition = Vector2(x, y)
                println(startingPosition)
            })
            addAction(Actions.forever(
                attackSequence1(crew) + attackSequence2(crew)
            ))
        }
        addAction(sequence)
    }

    private fun attackSequence2(crew: Crew): SequenceAction {
        val sinDuration = 5f
        val sinAction = object : RunnableAction() {
            var accDelta = 0f
            override fun act(delta: Float): Boolean {
                accDelta += delta
                this@Boss.y = startingPosition.y + sin(accDelta * 3f) * 50f
                return accDelta > sinDuration
            }
        }
        val delay = 0.15f
        val attackAction = Actions.repeat((sinDuration / delay).toInt(), Actions.delay(delay, Actions.run {
            // val myPos = Vector2(x,y)
            val enemyBullet = EnemyBullet(Vector2(-1f, 0f)).apply { centerOn(this@Boss) }
            crew.addMember(enemyBullet)
        }))
        return Actions.sequence(
            Actions.parallel(
                sinAction,
                attackAction
            ),
            Actions.moveTo(startingPosition.x, startingPosition.y, 1f)
        )
    }

    private fun attackSequence1(crew: Crew): SequenceAction {
        return Actions.sequence(
            Actions.delay(1f),
            Actions.run {
                repeat(5) {
                    val spawnY = (-Globals.WORLD_HEIGHT / 2) + ((it + 2) * distanceBetweenLanes)
                    val spawnX = 0f
                    val heatSeekingBullet = HeatSeakingBullet(Vector2(spawnX, spawnY)).apply {
                        centerOn(this@Boss)
                        y + 2f * (it - 1)
                    }
                    crew.addMember(heatSeekingBullet)
                }
            },
            Actions.delay(3f),
            Actions.moveBy(0f, 20f, 0.3f),
            Actions.delay(0.1f),
            Actions.run {
                repeat(3) {
                    val spawnY = (-Globals.WORLD_HEIGHT / 2) + ((8 - it) * distanceBetweenLanes)
                    val spawnX = this@Boss.x + this@Boss.width / 2f
                    val enemyBullet = EnemyBullet(Vector2(-1f, 0f)).apply { setPosition(spawnX, spawnY) }
                    crew.addMember(enemyBullet)
                }
            },
            Actions.delay(0.1f),
            Actions.moveBy(0f, -20f, 0.3f),
            Actions.delay(0.1f),
            Actions.run {
                repeat(3) {
                    val spawnY = (-Globals.WORLD_HEIGHT / 2) + ((5 - it) * distanceBetweenLanes)
                    val spawnX = this@Boss.x + this@Boss.width / 2f
                    val enemyBullet = EnemyBullet(Vector2(-1f, 0f)).apply { setPosition(spawnX, spawnY) }
                    crew.addMember(enemyBullet)
                }
            },
            Actions.delay(0.1f),
            Actions.moveBy(0f, -20f, 0.3f),
            Actions.delay(0.1f),
            Actions.run {
                repeat(3) {
                    val spawnY = (-Globals.WORLD_HEIGHT / 2) + ((3 - it) * distanceBetweenLanes)
                    val spawnX = this@Boss.x + this@Boss.width / 2f
                    val enemyBullet = EnemyBullet(Vector2(-1f, 0f)).apply { setPosition(spawnX, spawnY) }
                    crew.addMember(enemyBullet)
                }
            },
            Actions.delay(0.1f),
            Actions.moveBy(0f, 20f, 0.3f),
        )
    }

    private fun getDirectionTowardsPlayer(myPos: Vector2 = Vector2(x, y)): Vector2 {
        val playerPos = crew?.getAllOf<Player>()?.firstOrNull()?.pos() ?: return Vector2()
        return (playerPos - myPos).nor()
    }

    fun loseHealth() {
        currentHealth--
        if (canFlicker) {
            canFlicker = false
            val flickerDuration = addFlickerAction(0.05f, 6)
            addAction(Actions.delay(flickerDuration, Actions.run { canFlicker = true }))
        }
    }

    companion object {
        @Asset
        private val idleTexture = AssetDescriptor(Assets.Boss.idleAnimation, Texture::class.java)

        val idleAnimation by lazy { createAnimation(idleTexture.get(), 1, 2, 1f, true) }
    }
}
