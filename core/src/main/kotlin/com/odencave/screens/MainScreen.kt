package com.odencave.i18n.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.SFX
import com.odencave.entities.Entity
import com.odencave.entities.ScoreHandler
import com.odencave.entities.enemy.Enemy
import com.odencave.entities.enemy.Enemy.Companion.moveStraightEnemy
import com.odencave.entities.enemy.SandyEnemy
import com.odencave.entities.player.HealthIndicator
import com.odencave.entities.player.Player
import com.odencave.entities.player.PlayerBullet
import com.odencave.i18n.entities.enemy.spawner.EnemySpawner
import com.odencave.i18n.entities.enemy.spawner.EnemySpawner.Companion.LANE_COUNT
import com.odencave.i18n.entities.enemy.spawner.SpawnConfiguration
import com.odencave.i18n.gaia.base.BackgroundGrid
import com.odencave.i18n.gaia.ui.shaders.Shaders
import com.odencave.ui.MapModal
import gaia.Globals
import gaia.managers.MegaManagers
import gaia.managers.assets.AssetManager.Companion.get
import gaia.managers.input.ActionListener
import gaia.ui.BasicScreen
import gaia.ui.utils.alignLeft
import gaia.ui.utils.alignLeftToRightOf
import gaia.ui.utils.alignTop
import gaia.ui.utils.alignTopToBottomOf


class MainScreen : BasicScreen("Main") {

    lateinit var player: Player

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
            shouldDraw = false
            x -= 30f
        }
        val spawner = getSpawner()
        crew.addMembers(spawner)
        backgroundCrew.addMember(BackgroundGrid())

        // intro sequence
        if (!Globals.skipIntro) {
            val safeCrew = crew
            val map = MapModal().apply {
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
                    Actions.delay(5f),
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
            spawner.start()
        }
        val scoreHandler = ScoreHandler().apply {
            alignTop(-2f)
            alignLeft(8f)
        }
        val healthIndicator = HealthIndicator(player).apply {
            alignTopToBottomOf(scoreHandler, -12f)
            alignLeft(8f)
        }
        crew.addMembers(scoreHandler, healthIndicator)

    }

    private fun getSpawner(): EnemySpawner {
        return EnemySpawner().apply {
            wave(1) {
                wave1()
            }
            wave(2) {
                wave2()
            }
            wave(3) {
                wave3()
            }
        }
    }

    private fun EnemySpawner.wave3() {
        // ramp up difficulty
        val lane1Actions = (0..5).flatMap {
            listOf(Actions.delay(0.5f), addEnemyAction(SpawnConfiguration(moveStraightEnemy(70f), 0)))
        }
        val lane2Actions = listOf(Actions.delay(1f)) + (0..5).flatMap {
            listOf(Actions.delay(0.5f), addEnemyAction(SpawnConfiguration(moveStraightEnemy(70f), 2)))
        }
        val lane3Actions = listOf(Actions.delay(3f)) + (0..5).flatMap {
            listOf(Actions.delay(0.5f), addEnemyAction(SpawnConfiguration(moveStraightEnemy(70f), 4)))
        }
        val lane4Actions = listOf(Actions.delay(2f)) + (0..5).flatMap {
            listOf(Actions.delay(0.5f), addEnemyAction(SpawnConfiguration(moveStraightEnemy(70f), 6)))
        }
        addActionToSequence(
            Actions.parallel(
                Actions.sequence(*lane1Actions.toTypedArray()),
                Actions.sequence(*lane2Actions.toTypedArray()),
                Actions.sequence(*lane3Actions.toTypedArray()),
                Actions.sequence(*lane4Actions.toTypedArray()),
            )
        )
    }

    private fun EnemySpawner.wave2() {
        // Sandy introduction
        addEnemy(
            listOf(
                SpawnConfiguration(
                    SandyEnemy(),
                    5
                )
            ),
            2f
        )
        wait(1.5f)
        addEnemy(
            listOf(
                SpawnConfiguration(
                    SandyEnemy(),
                    2
                )
            )
        )
        wait(1.5f)
        addEnemy(
            listOf(
                SpawnConfiguration(
                    SandyEnemy(),
                    3
                )
            )
        )
        wait(5f)
    }

    private fun EnemySpawner.wave1() {
        repeat(LANE_COUNT) {
            addEnemy(
                listOf(
                    SpawnConfiguration(
                        Enemy().apply {
                            moveStraight()
                        },
                        it
                    ),
                ),
                if (it == 0) 0f else 0.5f
            )
        }
        wait(2f)
        repeat(LANE_COUNT) {
            addEnemy(
                listOf(
                    SpawnConfiguration(
                        Enemy().apply {
                            moveStraight()
                        },
                        8 - it
                    ),
                ),
                if (it == 0) 0f else 0.5f
            )
        }
        wait(2f)
        addEnemy(
            listOf(
                SpawnConfiguration(moveStraightEnemy(), 2),
                SpawnConfiguration(moveStraightEnemy(), 6),
            )
        )
        addEnemy(
            listOf(
                SpawnConfiguration(moveStraightEnemy(), 3),
                SpawnConfiguration(moveStraightEnemy(), 4),
            ),
            2f
        )
        addEnemy(
            listOf(
                SpawnConfiguration(moveStraightEnemy(), 5),
                SpawnConfiguration(moveStraightEnemy(), 7),
            ),
            2f
        )
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
                    val bullet = PlayerBullet().apply {
                        centerOn(player)
                        alignLeftToRightOf(player, 1f)
                    }
                    crew.addMember(bullet)
                    shootDebouncerReady = false
                    MegaManagers.soundManager.playSFXRandomPitch(SFX.playerBulletSounds.random().get())
                    MegaManagers.screenManager.addGlobalAction(Actions.delay(0.3f, Actions.run {
                        shootDebouncerReady = true
                    }))
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
