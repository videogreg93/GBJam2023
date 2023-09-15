package com.odencave.i18n.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.entities.Entity
import com.odencave.i18n.entities.enemy.spawner.EnemySpawner
import com.odencave.i18n.entities.enemy.spawner.SpawnConfiguration
import com.odencave.entities.player.Player
import com.odencave.entities.player.PlayerBullet
import com.odencave.i18n.gaia.base.BackgroundGrid
import com.odencave.i18n.gaia.ui.shaders.Shaders
import com.odencave.i18n.models.Palette
import gaia.Globals
import gaia.managers.MegaManagers
import gaia.managers.input.ActionListener
import gaia.ui.BasicScreen
import gaia.ui.utils.alignLeft
import gaia.ui.utils.alignLeftToRightOf
import gaia.utils.wrappingCursor

class MainScreen : BasicScreen("Main") {

    lateinit var player: Player

    private var selectedPaletteIndex: Int = 0
        set(value) {
            field = Palette.allPalettes.wrappingCursor(value)
        }
    private val selectedPalette: Palette
        get() = Palette.allPalettes[selectedPaletteIndex]

    // controls stuff
    var isUpPressed = false
    var isDownPressed = false
    var isRightPressed = false
    var isLeftPressed = false
    var shootDebouncerReady = true

    override fun firstShown() {
        super.firstShown()
        batch.shader = Shaders.paletteShader
        player = Player().apply {
            center()
            alignLeft(10f)
            x -= 30f
        }
        val spawner = EnemySpawner().apply {
            repeat(9) {
                addEnemy(
                    listOf(
                        SpawnConfiguration(
                            com.odencave.entities.enemy.Enemy().apply {
                                moveStraight()
                            },
                            it
                        ),
                    ),
                    0.5f
                )
            }
        }
        crew.addMembers(player, spawner)
        backgroundCrew.addMember(BackgroundGrid())

        player.addAction(
            Actions.sequence(
                Actions.run {
                    MegaManagers.inputActionManager.disableAllInputs()
                },
                Actions.moveBy(50f, 0f, 2.2f, Interpolation.fastSlow),
                Actions.delay(0.2f),
                Actions.run {
                    MegaManagers.inputActionManager.enableAllInputs()
                    player.canBeOutOfBounds = false
                },
                Actions.run {
                    spawner.start()
                }
            )
        )
    }

    override fun render(delta: Float) {
        super.render(delta)
        val colors = selectedPalette.colorsSortedByLightness()
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
                    val bullet = PlayerBullet().apply {
                        centerOn(player)
                        alignLeftToRightOf(player, 1f)
                    }
                    crew.addMember(bullet)
                    shootDebouncerReady = false
                    MegaManagers.screenManager.addGlobalAction(Actions.delay(0.3f, Actions.run {
                        shootDebouncerReady = true
                    }))
                }
            }

            ActionListener.InputAction.ONE -> updateResolution(1)
            ActionListener.InputAction.TWO -> updateResolution(2)
            ActionListener.InputAction.THREE -> updateResolution(4)
            ActionListener.InputAction.FOUR -> updateResolution(8)
            ActionListener.InputAction.ZERO -> {
                selectedPaletteIndex++
                Globals.currentBackgroundColor = selectedPalette.color4
            }

            else -> return false
        }
        return true
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
}
