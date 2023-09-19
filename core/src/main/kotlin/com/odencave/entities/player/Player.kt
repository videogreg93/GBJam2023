package com.odencave.entities.player

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.odencave.SFX
import com.odencave.assets.Assets
import com.odencave.entities.Entity
import com.odencave.entities.enemy.Enemy
import com.odencave.entities.enemy.EnemyBullet
import gaia.Globals
import gaia.actions.CameraShakeAction
import gaia.managers.MegaManagers
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get
import gaia.utils.FloatLerpAction
import kotlin.math.abs

class Player : Entity(playerTexture.get()) {
    var xSpeed = 0
    var ySpeed = 0
    var currentSpeed = DEFAULT_SPEED
    var canBeOutOfBounds = true
    var currentHealth = 3

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

    override fun onCollision(other: Entity) {
        if (other is Enemy) {
            MegaManagers.screenManager.getCurrentScreen()?.shakeCamera(0.2f, 2f)
            other.removeFromCrew()
            if (!Globals.godMode) {
                MegaManagers.soundManager.playSFXRandomPitch(SFX.playerHit.get())
                val hitStunAction = FloatLerpAction.createLerpAction(
                    0.7f, 1f, 1.5f, Interpolation.slowFast
                ) {
                    Globals.gameSpeed = it
                }
                MegaManagers.screenManager.addGlobalAction(hitStunAction)
                currentHealth--
            }
        } else if (other is EnemyBullet) {
            MegaManagers.screenManager.getCurrentScreen()?.shakeCamera(0.2f, 2f)
            other.removeFromCrew()
            if (!Globals.godMode) {
                MegaManagers.soundManager.playSFXRandomPitch(SFX.playerHit.get())
                val hitStunAction = FloatLerpAction.createLerpAction(
                    0.7f, 1f, 1.5f, Interpolation.slowFast
                ) {
                    Globals.gameSpeed = it
                }
                MegaManagers.screenManager.addGlobalAction(hitStunAction)
                currentHealth--
            }
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        val previousPosition = Vector2(x,y)
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


    companion object {
        private const val DEFAULT_SPEED = 44

        @Asset
        val playerTexture = AssetDescriptor(Assets.Player.player, Texture::class.java)
    }
}
