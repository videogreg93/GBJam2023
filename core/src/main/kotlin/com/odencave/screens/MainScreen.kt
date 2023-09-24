package com.odencave.i18n.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.SFX
import com.odencave.ScoreManager
import com.odencave.assets.Assets
import com.odencave.entities.Entity
import com.odencave.entities.enemy.spawner.EndLevelEvent
import com.odencave.entities.enemy.spawner.SpawnerLevels
import com.odencave.entities.player.HealthIndicator
import com.odencave.entities.player.Player
import com.odencave.entities.player.PlayerBullet
import com.odencave.entities.player.ScoreBar
import com.odencave.events.PlayerDeathEvent
import com.odencave.i18n.gaia.base.BackgroundGrid
import com.odencave.i18n.gaia.ui.shaders.Shaders
import com.odencave.models.ShipUpgrade
import com.odencave.screens.DeathScreen
import com.odencave.ui.MapModal
import gaia.Globals
import gaia.managers.MegaManagers
import gaia.managers.assets.Asset
import gaia.managers.assets.AssetManager.Companion.get
import gaia.managers.events.EventInstance
import gaia.managers.events.EventListener
import gaia.managers.input.ActionListener
import gaia.ui.BasicScreen
import gaia.ui.utils.*


class MainScreen(val player: Player = Player(), val showMapScreen: Boolean = true) : BasicScreen("Main"), EventListener<EventInstance> {

    // controls stuff
    var isUpPressed = false
    var isDownPressed = false
    var isRightPressed = false
    var isLeftPressed = false
    var shootDebouncerReady = true

    override fun firstShown() {
        super.firstShown()
        MegaManagers.eventManager.subscribeTo<EndLevelEvent>(this)
        MegaManagers.eventManager.subscribeTo<PlayerDeathEvent>(this)
        batch.shader = Shaders.paletteShader
        player.center()
        player.alignLeft(10f)
        player.shouldDraw = false
        player.x -= 30f
        val spawner = when {
            Globals.world4Unlocked -> SpawnerLevels.World4()
            Globals.world3Unlocked -> SpawnerLevels.World4()
            Globals.world2Unlocked -> SpawnerLevels.World2()
            else -> SpawnerLevels.World1()
        }
        crew.addMembers(spawner)
        backgroundCrew.addMember(BackgroundGrid())

        // intro sequence
        if (!Globals.skipIntro && showMapScreen) {
            val safeCrew = crew
            val selectedIndex = when {
                Globals.world4Unlocked -> 3
                Globals.world3Unlocked -> 2
                Globals.world2Unlocked -> 1
                else -> 0
            }
            val map = MapModal(selectedIndex).apply {
                center()
                alignTop()
                y += Globals.WORLD_HEIGHT / 2
                addAction(
                    Actions.sequence(
                        Actions.moveBy(0f, -Globals.WORLD_HEIGHT / 2, 2.5f, Interpolation.fastSlow),
                        Actions.run {
                            showShipCursor()
                        },
                        Actions.delay(3f),
                        Actions.run {
                            hideShipCursor()
                            safeCrew.addMember(player)
                            MegaManagers.soundManager.playMusicWithIntro(BGM.get(), BGMIntro.get(), true)
                        },
                        Actions.moveBy(0f, Globals.WORLD_HEIGHT / 2, 2f, Interpolation.slowFast),
                        Actions.run {
                            removeFromCrew()
                        }
                    )
                )
            }
            crew.addMember(map)
            player.addAction(
                Actions.sequence(
                    Actions.run {
                        MegaManagers.inputActionManager.disableAllInputs()
                        player.shouldDraw = true
                    },
                    Actions.moveBy(50f, 0f, 2.2f, Interpolation.fastSlow),
                    Actions.delay(0.2f),
                    Actions.run {
                        MegaManagers.inputActionManager.enableAllInputs()
                        player.canBeOutOfBounds = false
                    },
                    Actions.delay(4f),
                    Actions.run {
                        // this is when the game actually starts
                        spawner.start()
                    }
                )
            )
        } else {
            // dev intro skipped
            player.shouldDraw = true
            player.x += 50f
            player.canBeOutOfBounds = false
            crew.addMember(player)
            MegaManagers.soundManager.playMusicWithIntro(BGM.get(), BGMIntro.get(), true)
            spawner.start()
        }
        val scoreHandler = ScoreBar().apply {
            alignTop(-2f)
            alignLeft(8f)
        }
        val healthIndicator = HealthIndicator(player).apply {
            alignTopToBottomOf(scoreHandler, -12f)
            alignLeft(8f)
        }
        crew.addMembers(scoreHandler, healthIndicator)

    }

    override fun onEvent(event: EventInstance) {
        when (event) {
            is PlayerDeathEvent -> {
                MegaManagers.soundManager.stopCurrentMusic()
                player.removeFromCrew()
                MegaManagers.screenManager.addGlobalAction(
                    Actions.delay(2f, Actions.run {
                        MegaManagers.screenManager.changeScreen(DeathScreen(player))
                    })
                )
            }

            is EndLevelEvent -> {
                MegaManagers.inputActionManager.disableAllInputs()
                player.stop()
                val dest = player.calculatePositionFor {
                    center()
                }
                player.addAction(
                    Actions.sequence(
                        Actions.run {
                            MegaManagers.soundManager.stopCurrentMusic()
                        },
                        Actions.moveTo(dest.x, dest.y, 1f, Interpolation.fastSlow),
                        Actions.run {
                            MegaManagers.soundManager.playSFX(SFX.levelComplete.get())
                        },
                        Actions.delay(2f),
                        Actions.moveBy(Globals.WORLD_WIDTH / 2f + 10f, 0f, 1.5f, Interpolation.fastSlow),
                        Actions.delay(1f),
                        Actions.run {
                            // Update which level we're on
                            when {
                                Globals.world4Unlocked -> {
                                    // TODO game complete
                                }

                                Globals.world2Unlocked || Globals.world3Unlocked -> Globals.world4Unlocked = true
                                MegaManagers.getManager<ScoreManager>().currentTotalScore >= ScoreManager.SCORE_FOR_SECRET_LEVEL -> Globals.world3Unlocked
                                else -> Globals.world2Unlocked = true
                            }
                            MegaManagers.screenManager.changeScreen(MainScreen(player))
                            MegaManagers.inputActionManager.enableAllInputs()
                        }
                    )
                )
            }
        }
    }

    override fun render(delta: Float) {
        super.render(delta)
        val colors = Globals.selectedPalette.colorsSortedByLightness()
        batch.shader.setUniformf("inputColor1", colors[0])
        batch.shader.setUniformf("inputColor2", colors[1])
        batch.shader.setUniformf("inputColor3", colors[2])
        batch.shader.setUniformf("inputColor4", colors[3])
        checkCollisions()
    }

    override fun onAction(action: ActionListener.InputAction): Boolean {
        when (action) {
            ActionListener.InputAction.UP -> {
                isUpPressed = true
                player.moveUp()
            }

            ActionListener.InputAction.DOWN -> {
                isDownPressed = true
                player.moveDown()
            }

            ActionListener.InputAction.RIGHT -> {
                isRightPressed = true
                player.moveRight()
            }

            ActionListener.InputAction.LEFT -> {
                isLeftPressed = true
                player.moveLeft()
            }

            ActionListener.InputAction.SHOOT -> {
                if (shootDebouncerReady) {
                    when (player.shipUpgrade) {
                        ShipUpgrade.Upgrade1 -> shootUpgrade1()
                        ShipUpgrade.Upgrade2 -> shootUpgrade2()
                        ShipUpgrade.Upgrade3 -> shootNoUpgrade()
                        null -> shootNoUpgrade()
                    }
                }
            }

            ActionListener.InputAction.ONE -> updateResolution(1)
            ActionListener.InputAction.TWO -> updateResolution(2)
            ActionListener.InputAction.THREE -> updateResolution(4)
            ActionListener.InputAction.FOUR -> updateResolution(8)
            ActionListener.InputAction.SEVEN -> {
                if (Globals.godMode) {
                    Globals.godMode = false
                    Globals.gameSpeed = 1f
                } else {
                    Globals.godMode = true
                    Globals.gameSpeed = 5f
                }
            }

            ActionListener.InputAction.ZERO -> {
                Globals.selectedPaletteIndex++
                Globals.currentBackgroundColor = Globals.selectedPalette.color4
            }

            else -> return false
        }
        return true
    }

    private fun shootNoUpgrade() {
        val bullet = PlayerBullet().apply {
            centerOn(player)
            alignLeftToRightOf(player, 1f)
        }
        crew.addMember(bullet)
        shootDebouncerReady = false
        MegaManagers.soundManager.playSFXRandomPitch(SFX.playerBulletSounds.random().get(), -0.1f)
        MegaManagers.screenManager.addGlobalAction(Actions.delay(0.3f, Actions.run {
            shootDebouncerReady = true
        }))
    }

    private fun shootUpgrade1() {
        val bullet1 = PlayerBullet().apply {
            centerOn(player)
            alignBottomToTopOf(player)
            alignLeftToRightOf(player, 1f)
        }
        val bullet2 = PlayerBullet().apply {
            centerOn(player)
            alignTopToBottomOf(player)
            alignLeftToRightOf(player, 1f)
        }
        crew.addMembers(bullet1, bullet2)
        shootDebouncerReady = false
        MegaManagers.soundManager.playSFXRandomPitch(SFX.playerBulletSounds.random().get(), -0.1f)
        MegaManagers.screenManager.addGlobalAction(Actions.delay(0.3f, Actions.run {
            shootDebouncerReady = true
        }))
    }

    private fun shootUpgrade2() {
        val bullet1 = PlayerBullet().apply {
            centerOn(player)
            alignBottomToTopOf(player)
        }
        val bullet2 = PlayerBullet().apply {
            centerOn(player)
            alignTopToBottomOf(player)
        }
        val bullet3 = PlayerBullet().apply {
            centerOn(player)
            alignLeftToRightOf(player, 1f)
        }
        crew.addMembers(bullet1, bullet2, bullet3)
        shootDebouncerReady = false
        MegaManagers.soundManager.playSFX(SFX.playerBulletLevel3.get())
        MegaManagers.screenManager.addGlobalAction(Actions.delay(0.3f, Actions.run {
            shootDebouncerReady = true
        }))
    }

    override fun onActionReleased(action: ActionListener.InputAction): Boolean {
        when (action) {
            ActionListener.InputAction.UP -> {
                isUpPressed = false
                if (isDownPressed) {
                    player.moveDown()
                } else {
                    player.stopVertical()
                }
            }

            ActionListener.InputAction.DOWN -> {
                isDownPressed = false
                if (isUpPressed) {
                    player.moveUp()
                } else {
                    player.stopVertical()
                }
            }

            ActionListener.InputAction.RIGHT -> {
                isRightPressed = false
                if (isLeftPressed) {
                    player.moveLeft()
                } else {
                    player.stopHorizontal()
                }
            }

            ActionListener.InputAction.LEFT -> {
                isLeftPressed = false
                if (isRightPressed) {
                    player.moveRight()
                } else {
                    player.stopHorizontal()
                }
            }

            else -> return false
        }

        return true
    }

    private fun checkCollisions() {
        val entities = crew.getAllOf<Entity>()
        val size = entities.size
        for (i in 0 until size) {
            val entity = entities[i]
            for (j in i + 1 until size) {
                val other = entities[j]
                if (entity.isCollidingWith(other)) {
                    entity.onCollision(other)
                    other.onCollision(entity)
                }
            }
        }
    }

    private fun updateResolution(multiplier: Int) {
        Gdx.graphics.setWindowedMode(
            (Globals.WORLD_WIDTH * multiplier).toInt(),
            (Globals.WORLD_HEIGHT * multiplier).toInt()
        )
    }

    companion object {
        @Asset
        val BGM = AssetDescriptor(Assets.ZenithGameTheme_ogg_sound, Music::class.java)

        @Asset
        val BGMIntro = AssetDescriptor(Assets.ZenithGameThemeIntro_ogg_sound, Music::class.java)
    }
}
