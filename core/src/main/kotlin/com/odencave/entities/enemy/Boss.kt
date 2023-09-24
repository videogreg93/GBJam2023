package com.odencave.entities.enemy

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.assets.Assets
import gaia.base.Crew
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get

class Boss : Enemy(idleTexture.get()) {

    private var canFlicker = true

    val maxHealth = 100
    var currentHealth = maxHealth
        set(value) {
            field = value.coerceAtLeast(0)
        }

    init {
        setPosition(1000f,1000f)
        updateSprite()
        shouldDraw = false
    }

    override fun onAddedToCrew(crew: Crew) {
        super.onAddedToCrew(crew)
        val sequence = Actions.sequence().apply {
            addAction(Actions.run { shouldDraw = true })
            addAction(Actions.moveBy(-(width / 2f + 20), 0f, 4f, Interpolation.fastSlow))
            addAction(Actions.delay(0.5f))
        }
        addAction(sequence)
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
        private val idleTexture = AssetDescriptor(Assets.Boss.idle, Texture::class.java)
    }
}
