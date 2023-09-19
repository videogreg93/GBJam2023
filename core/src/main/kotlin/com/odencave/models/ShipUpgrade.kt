package com.odencave.models

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.Texture
import com.odencave.assets.Assets
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get

sealed class ShipUpgrade(val scoreThreshold: Int,val texture: AssetDescriptor<Texture>) {
    object Upgrade1 : ShipUpgrade(200, playerTexture)
    object Upgrade2 : ShipUpgrade(200, playerTexture)
    object Upgrade3 : ShipUpgrade(250, playerTexture)

    companion object {
        @Asset
        val playerTexture = AssetDescriptor(Assets.Player.player, Texture::class.java)
    }
}
