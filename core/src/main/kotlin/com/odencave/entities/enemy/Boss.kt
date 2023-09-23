package com.odencave.entities.enemy

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.assets.Assets
import gaia.base.Crew
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get

class Boss: Enemy(idleTexture.get()) {

    override fun onAddedToCrew(crew: Crew) {
        super.onAddedToCrew(crew)
        val sequence = Actions.sequence().apply {
            addAction(Actions.moveBy(-50f, 0f, 4f, Interpolation.fastSlow))
            addAction(Actions.delay(0.5f))
        }
        addAction(sequence)
    }

    companion object {
        @Asset
        private val idleTexture = AssetDescriptor(Assets.Boss.idle, Texture::class.java)
    }
}
