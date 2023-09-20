package com.odencave

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.audio.Sound
import com.odencave.assets.Assets
import gaia.managers.assets.Asset

object SFX {
    @Asset
    val playerBullet1SFX = AssetDescriptor(Assets.Sounds.bullet1_ogg_sound, Sound::class.java)

    @Asset
    val playerBullet2SFX = AssetDescriptor(Assets.Sounds.bullet2_ogg_sound, Sound::class.java)

    val playerBulletSounds = listOf(playerBullet1SFX, playerBullet2SFX)

    @Asset
    val playerHit = AssetDescriptor(Assets.Sounds.playerHit_ogg_sound, Sound::class.java)

    @Asset
    val enemyHit = AssetDescriptor(Assets.Sounds.enemyHit_ogg_sound, Sound::class.java)

    @Asset
    val playerUpgrade = AssetDescriptor(Assets.Sounds.upgrade_ogg_sound, Sound::class.java)
}
