package com.odencave.i18n.entities.player

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.odencave.assets.Assets
import com.odencave.i18n.entities.Entity
import entities.player.PlayerBullet
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get
import kotlin.math.abs

class Player : Entity(playerTexture.get()) {
    var xSpeed = 0
    var ySpeed = 0
    var currentSpeed = DEFAULT_SPEED

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

    override fun act(delta: Float) {
        super.act(delta)
        val previousPosition = Vector2(x,y)
        val speedVector = Vector2(xSpeed.toFloat(), ySpeed.toFloat()).nor()
        x += (xSpeed * abs(speedVector.x)) * delta
        if (isPartlyOutOfBounds()) {
            x = previousPosition.x
        }
        y += (ySpeed * abs(speedVector.y)) * delta
        if (isPartlyOutOfBounds()) {
            y = previousPosition.y
        }
    }


    companion object {
        private const val DEFAULT_SPEED = 50

        @Asset
        private val playerTexture = AssetDescriptor(Assets.Player.player, Texture::class.java)
    }
}
