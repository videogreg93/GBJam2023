package com.odencave.i18n.screens

import com.badlogic.gdx.Gdx
import com.odencave.i18n.entities.enemy.Enemy
import com.odencave.i18n.entities.enemy.spawner.EnemySpawner
import com.odencave.i18n.entities.enemy.spawner.SpawnConfiguration
import com.odencave.i18n.entities.player.Player
import com.odencave.i18n.gaia.base.BackgroundGrid
import com.odencave.i18n.gaia.ui.shaders.Shaders
import com.odencave.i18n.models.Palette
import gaia.Globals
import gaia.managers.input.ActionListener
import gaia.ui.BasicScreen
import gaia.ui.utils.alignLeft
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

    override fun firstShown() {
        super.firstShown()
        batch.shader = Shaders.paletteShader
        player = Player().apply {
            center()
            alignLeft(10f)
        }
        val spawner = EnemySpawner().apply {
            repeat(9) {
                addEnemy(
                    listOf(
                        SpawnConfiguration(
                            Enemy().apply {
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
        spawner.start()
    }

    override fun render(delta: Float) {
        super.render(delta)
        val colors = selectedPalette.colorsSortedByLightness()
        batch.shader.setUniformf("inputColor1", colors[0])
        batch.shader.setUniformf("inputColor2", colors[1])
        batch.shader.setUniformf("inputColor3", colors[2])
        batch.shader.setUniformf("inputColor4", colors[3])
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

    private fun updateResolution(multiplier: Int) {
        Gdx.graphics.setWindowedMode(
            (Globals.WORLD_WIDTH * multiplier).toInt(),
            (Globals.WORLD_HEIGHT * multiplier).toInt()
        )
    }
}
