package com.odencave.ui

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.assets.Assets
import gaia.Globals
import gaia.base.BaseActor
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get
import gaia.ui.utils.*

class MapModal(val selectedWorldIndex: Int = 0): BaseActor(background.get()) {
    private val world1 = BaseActor(world1Asset.get())
    private val world2 = BaseActor(if (Globals.world2Unlocked) world2Asset.get() else worldLocked.get())
    private val world3 = BaseActor(if (Globals.world3Unlocked) world3Asset.get() else worldLocked.get())
    private val world4 = BaseActor(if (Globals.world4Unlocked) finalWorldAsset.get() else worldLocked.get())
    private val allWorlds = listOf(world1, world2, world3, world4)

    private val shipCursor = BaseActor(shipCursorAsset.get(), 1000f, 1000f).apply {
        drawIndex = -2000
    }

    init {
        drawIndex = -1000
        allWorlds.forEach {
            it.drawIndex = drawIndex - 1
        }
        children.addAll(allWorlds)
    }

    override fun act(delta: Float) {
        super.act(delta)
        world1.centerOn(this)
        world1.alignLeftToLeftOf(this, 30f)
        world2.centerOn(this)
        world2.alignTopToTopOf(this, -10f)
        world3.centerOn(this)
        world3.alignBottomToBottomOf(this, 10f)
        world4.centerOn(this)
        world4.alignRightToRightOf(this, -30f)
    }

    fun showShipCursor() {
        val selectedWorld = allWorlds[selectedWorldIndex]
        shipCursor.centerOn(selectedWorld)
        shipCursor.alignRightToLeftOf(selectedWorld)
        shipCursor.x -= 12f
        val resetPosition = Vector2(shipCursor.x, shipCursor.y)
        shipCursor.shouldDraw = true
        shipCursor.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.moveTo(resetPosition.x, resetPosition.y),
                    Actions.delay(0.1f),
                    Actions.moveBy(10f, 0f, 1f, Interpolation.fastSlow),
                )
            )
        )
        crew?.addMember(shipCursor)
    }

    fun hideShipCursor() {
        shipCursor.removeFromCrew()
    }

    companion object {
        @Asset
        private val background = AssetDescriptor(Assets.mapBackground, Texture::class.java)

        @Asset
        private val world1Asset = AssetDescriptor(Assets.World.world1, Texture::class.java)

        @Asset
        private val world2Asset = AssetDescriptor(Assets.World.world2, Texture::class.java)

        @Asset
        private val finalWorldAsset = AssetDescriptor(Assets.World.boss, Texture::class.java)

        @Asset
        private val world3Asset = AssetDescriptor(Assets.World.world3, Texture::class.java)

        @Asset
        private val worldLocked = AssetDescriptor(Assets.World.worldLocked, Texture::class.java)

        @Asset
        private val shipCursorAsset = AssetDescriptor(Assets.World.tinyShip, Texture::class.java)
    }
}
