package com.odencave.entities.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.SFX
import com.odencave.ScoreManager
import com.odencave.assets.Assets
import com.odencave.entities.Entity
import com.odencave.entities.enemy.boss.Boss
import com.odencave.entities.enemy.Enemy
import com.odencave.entities.enemy.EnemyBullet
import com.odencave.events.PlayerDeathEvent
import com.odencave.models.ShipUpgrade
import gaia.Globals
import gaia.base.Crew
import gaia.managers.MegaManagers
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get
import gaia.utils.FloatLerpAction
import kotlin.math.abs

class Player : Entity(playerSmallTexture.get()) {
    var xSpeed = 0
    var ySpeed = 0
    val currentSpeed
        get() = DEFAULT_SPEED
    var canBeOutOfBounds = true
    var currentHealth = 2
    var invincible = false
    var shipUpgrade: ShipUpgrade? = null

    fun stop() {
        xSpeed = 0
        ySpeed = 0
    }

    fun stopHorizontal() {
        xSpeed = 0
    }

    fun stopVertical() {
        ySpeed = 0
    }

    fun moveUp() {
        ySpeed = currentSpeed
    }

    fun moveDown() {
        ySpeed = -currentSpeed
    }

    fun moveRight() {
        xSpeed = currentSpeed
    }

    fun moveLeft() {
        xSpeed = -currentSpeed
    }

    override fun onAddedToCrew(crew: Crew) {
        super.onAddedToCrew(crew)
    }

    override fun onCollision(other: Entity) {
        when (other) {
            is Enemy, is EnemyBullet -> {
                if (!invincible) {
                    handleHit(other)
                }
            }
        }
    }

    private fun handleHit(other: Entity) {
        MegaManagers.screenManager.getCurrentScreen()?.shakeCamera(0.2f, 2f)
        if (other !is Boss) {
            other.removeFromCrew()
        }
        if (!Globals.godMode) {
            MegaManagers.getManager<ScoreManager>().downgrade()
            val invincibilityDuration = addFlickerAction(0.05f, 6)
            invincible = true
            addAction(Actions.delay(invincibilityDuration, Actions.run {
                invincible = false
                Gdx.app.log("PLAYER", "No longer invincible.")
            }))
            MegaManagers.soundManager.playSFXRandomPitch(SFX.playerHit.get())
            val hitStunAction = FloatLerpAction.createLerpAction(
                0.7f, 1f, 1.5f, Interpolation.slowFast
            ) {
                Globals.gameSpeed = it
            }
            MegaManagers.screenManager.addGlobalAction(hitStunAction)
            currentHealth--
            if (currentHealth <= 0) {
                MegaManagers.eventManager.sendEvent(PlayerDeathEvent(this))
            }
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        val previousPosition = Vector2(x, y)
        val speedVector = Vector2(xSpeed.toFloat(), ySpeed.toFloat()).nor()
        x += (xSpeed * abs(speedVector.x)) * delta
        if (!canBeOutOfBounds && isPartlyOutOfBounds()) {
            x = previousPosition.x
        }
        y += (ySpeed * abs(speedVector.y)) * delta
        if (!canBeOutOfBounds && isPartlyOutOfBounds()) {
            y = previousPosition.y
        }
    }

    fun upgradeShip(upgrade: ShipUpgrade) {
        shipUpgrade = upgrade
        val oldSprite = Sprite(sprite)
        Gdx.app.log("PLAYER", "Upgraded ship!")
        val flickDelay = 0.05f
        MegaManagers.soundManager.playSFX(SFX.playerUpgrade.get())
        addAction(
            Actions.sequence(
                Actions.repeat(6, Actions.sequence(
                    Actions.run {
                        sprite = Sprite(upgrade.texture.get())
                    },
                    Actions.delay(flickDelay),
                    Actions.run {
                        sprite = oldSprite
                    },
                    Actions.delay(flickDelay)
                )),
                Actions.run {
                    sprite = Sprite(upgrade.texture.get())
                }
            )
        )
    }

    fun downgradeShip(upgrade: ShipUpgrade?) {
        shipUpgrade = upgrade
        Gdx.app.log("PLAYER", "Downgraded ship :(")
        sprite = Sprite(upgrade?.texture?.get() ?: playerSmallTexture.get())
        // todo change texture
    }

    fun resetHealth() {
        currentHealth = 2
    }


    companion object {
        private val DEFAULT_SPEED = 44

        @Asset
        val playerTexture = AssetDescriptor(Assets.Player.player, Texture::class.java)

        @Asset
        val playerSmallTexture = AssetDescriptor(Assets.Player.playerSmall, Texture::class.java)
    }
}
