package com.odencave.entities.enemy

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.assets.Assets
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get

class SandyEnemy: Enemy(sandyAsset.get()) {
    val moveAmount = 50f

    init {
        val sequence = Actions.sequence().apply {
            addAction(Actions.moveBy(-moveAmount, 0f, 2f, Interpolation.fastSlow))
            addAction(Actions.delay(2f))
            // TODO shoot player
            addAction(Actions.moveBy(moveAmount + 10, 0f, 2f, Interpolation.slowFast))
        }
        addAction(sequence)
    }

    companion object {
        @Asset
        private val sandyAsset = AssetDescriptor(Assets.Enemy.sandy, Texture::class.java)
    }
}
