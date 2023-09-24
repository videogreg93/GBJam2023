package com.odencave.entities.enemy

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.assets.Assets
import com.odencave.entities.player.Player
import gaia.Globals
import gaia.base.Crew
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get
import gaia.ui.utils.alignRightToLeftOf
import ktx.math.minus

class SandyEnemy(val moveAmount: Float = 50f, arriveFromBack: Boolean = false) : Enemy(sandyAsset.get(), arriveFromBack) {


    init {
        scoreValue = 15
        val signMultiplier = if (arriveFromBack) -1 else 1
        val sequence = Actions.sequence().apply {
            addAction(Actions.moveBy(-moveAmount * signMultiplier, 0f, 2f, Interpolation.fastSlow))
            addAction(Actions.delay(0.5f))
            repeat(3) {
                addAction(Actions.delay(0.15f))
                addAction(
                    Actions.run {
                        shootBullet()
                    })
            }
            addAction(Actions.moveBy((moveAmount + 10) * signMultiplier, 0f, 2f, Interpolation.slowFast))
            addAction(Actions.run { removeFromCrew() })
        }
        addAction(sequence)
    }

    private fun shootBullet() {
        val playerPos = crew?.getAllOf<Player>()?.firstOrNull()?.pos() ?: return
        val myPos = pos()
        val direction = (playerPos - myPos).nor()
        val bullet = EnemyBullet(direction)
        bullet.centerOn(this@SandyEnemy)
        bullet.alignRightToLeftOf(this@SandyEnemy)
        crew?.addMember(bullet)
    }

    companion object {
        @Asset
        private val sandyAsset = AssetDescriptor(Assets.Enemy.sandy, Texture::class.java)
    }
}
