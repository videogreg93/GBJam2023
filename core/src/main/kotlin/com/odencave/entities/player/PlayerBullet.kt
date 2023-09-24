package com.odencave.entities.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.odencave.SFX
import com.odencave.assets.Assets
import com.odencave.entities.Entity
import com.odencave.entities.enemy.boss.Boss
import com.odencave.entities.enemy.Enemy
import gaia.managers.MegaManagers
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get

class PlayerBullet : Entity(bulletAsset.get()) {
    var currentSpeed = DEFAULT_SPEED

    override fun act(delta: Float) {
        super.act(delta)
        x += currentSpeed * delta
        if (isCompletelyOutOfBounds()) {
            Gdx.app.log("PlayerBullet", "Removed because out of bounds")
            removeFromCrew()
        }
    }

    override fun onCollision(other: Entity) {
        when (other) {
            is Boss -> {
                removeFromCrew()
                other.loseHealth()
            }
            is Enemy -> {
                MegaManagers.soundManager.playSFXRandomPitch(SFX.enemyHit.get())
                other.destroy()
                removeFromCrew()
                Gdx.app.log("PlayerBullet", "Killed Enemy!!!")
            }
        }
    }

    companion object {

        private const val DEFAULT_SPEED = 80f

        @Asset
        private val bulletAsset = AssetDescriptor(Assets.Player.bullet, Texture::class.java)
    }
}
