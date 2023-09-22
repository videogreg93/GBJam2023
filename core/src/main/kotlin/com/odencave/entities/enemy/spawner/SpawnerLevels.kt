package com.odencave.entities.enemy.spawner

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.entities.enemy.Enemy
import com.odencave.entities.enemy.SandyEnemy
import com.odencave.i18n.entities.enemy.spawner.EnemySpawner
import com.odencave.i18n.entities.enemy.spawner.SpawnConfiguration
import gaia.utils.toSequence

object SpawnerLevels {
    fun World1(): EnemySpawner {
        fun EnemySpawner.wave4Part2() {
            val bottomLane = (0..10).flatMap {
                if (it % 2 == 0) {
                    listOf(
                        Actions.delay(
                            0.25f,
                            addEnemyAction(SpawnConfiguration(Enemy.moveStraightEnemy(Enemy.DEFAULT_ENEMY_MOVE_SPEED).apply {
                                moveLanes(1f, -1)
                            }, 2))
                        )
                    )
                } else {
                    listOf(
                        Actions.delay(
                            0.25f,
                            addEnemyAction(SpawnConfiguration(Enemy.moveStraightEnemy(Enemy.DEFAULT_ENEMY_MOVE_SPEED).apply {
                                moveLanes(1f, 2)
                            }, 1))
                        )
                    )
                }
            }
            addActionToSequence(
                Actions.parallel(
                    Actions.sequence(*bottomLane.toTypedArray()),
                )
            )
        }

        fun EnemySpawner.wave4() {
            repeat(4) {
                val enemy = Enemy.moveStraightEnemy(Enemy.FASTER_ENEMY_MOVE_SPEED).apply {
                    moveUpALane()
                }
                addEnemy(listOf(SpawnConfiguration(enemy, 2)), 0.5f)
            }
            wait(1f)
            repeat(4) {
                val enemy = Enemy.moveStraightEnemy(Enemy.FASTER_ENEMY_MOVE_SPEED).apply {
                    moveDownALane()
                }
                addEnemy(listOf(SpawnConfiguration(enemy, 5)), 0.5f)
            }
            val actions1 = (0..5).flatMap {
                listOf(
                    Actions.delay(
                        0.5f,
                        addEnemyAction(SpawnConfiguration(Enemy.moveStraightEnemy(Enemy.FASTER_ENEMY_MOVE_SPEED), 1))
                    )
                )
            }
            val actions2 = listOf(
                Actions.delay(1f),
                addEnemyAction(SpawnConfiguration(SandyEnemy(), 7))
            ) + (0..5).flatMap {
                listOf(
                    Actions.delay(
                        0.5f,
                        addEnemyAction(SpawnConfiguration(Enemy.moveStraightEnemy(Enemy.FASTER_ENEMY_MOVE_SPEED), 6))
                    )
                )
            }
            val actions3 = listOf(
                Actions.delay(2f),
                addEnemyAction(SpawnConfiguration(SandyEnemy(), 3)),
                Actions.delay(1f),
                addEnemyAction(SpawnConfiguration(SandyEnemy(), 5)),
                Actions.delay(1f, addEnemyAction(SpawnConfiguration(SandyEnemy(), 1))),
                Actions.delay(1f, addEnemyAction(SpawnConfiguration(SandyEnemy(), 4)))
            )
            addActionToSequence(
                Actions.parallel(
                    Actions.sequence(*actions1.toTypedArray()),
                    Actions.sequence(*actions2.toTypedArray()),
                    Actions.sequence(*actions3.toTypedArray()),
                )
            )
            wait(1f)

        }

        fun EnemySpawner.wave3() {
            // ramp up difficulty
            val lane1Actions = (0..5).flatMap {
                listOf(Actions.delay(0.5f), addEnemyAction(SpawnConfiguration(Enemy.moveStraightEnemy(70f), 0)))
            }
            val lane2Actions = listOf(Actions.delay(1f)) + (0..5).flatMap {
                listOf(Actions.delay(0.5f), addEnemyAction(SpawnConfiguration(Enemy.moveStraightEnemy(70f), 2)))
            }
            val lane4Actions = listOf(Actions.delay(2f)) + (0..5).flatMap {
                listOf(Actions.delay(0.5f), addEnemyAction(SpawnConfiguration(Enemy.moveStraightEnemy(70f), 6)))
            }
            addActionToSequence(
                Actions.parallel(
                    Actions.sequence(*lane1Actions.toTypedArray()),
                    Actions.sequence(*lane2Actions.toTypedArray()),
                    Actions.sequence(*lane4Actions.toTypedArray()),
                )
            )
        }

        fun EnemySpawner.wave2() {
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

        fun EnemySpawner.wave1() {
            repeat(EnemySpawner.LANE_COUNT) {
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
            repeat(EnemySpawner.LANE_COUNT) {
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
                    SpawnConfiguration(Enemy.moveStraightEnemy(), 2),
                    SpawnConfiguration(Enemy.moveStraightEnemy(), 6),
                )
            )
            addEnemy(
                listOf(
                    SpawnConfiguration(Enemy.moveStraightEnemy(), 3),
                    SpawnConfiguration(Enemy.moveStraightEnemy(), 4),
                ),
                2f
            )
            addEnemy(
                listOf(
                    SpawnConfiguration(Enemy.moveStraightEnemy(), 5),
                    SpawnConfiguration(Enemy.moveStraightEnemy(), 7),
                ),
                2f
            )
        }

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
            wave(4) {
                wave4()
                wave4Part2()
            }
            wait(5f)
            finishLevel()
        }
    }

    fun World2(): EnemySpawner {
        return EnemySpawner().apply {
            val sneakySandyAction = addEnemyAction(SpawnConfiguration(SandyEnemy(40f), 8))
            val actions1 = (0..5).flatMap {
                val enemy = Enemy.moveStraightEnemy(Enemy.FASTER_ENEMY_MOVE_SPEED).apply {
                    sineMovement()
                }
                listOf(Actions.delay(0.4f, addEnemyAction(SpawnConfiguration(enemy, 6))))
            }.toSequence()
            val actions2 = (listOf(Actions.delay(1f)) + (0..5).flatMap {
                val enemy = Enemy.moveStraightEnemy(Enemy.FASTER_ENEMY_MOVE_SPEED).apply {
                    sineMovement()
                }
                listOf(Actions.delay(0.4f, addEnemyAction(SpawnConfiguration(enemy, 2))))
            }).toSequence()

            val actions4 = (listOf(Actions.delay(2f)) + (0..5).flatMap {
                val enemy = Enemy.moveStraightEnemy(Enemy.FASTER_ENEMY_MOVE_SPEED + 20f).apply {
                    sineMovement(10f, 4f)
                }
                listOf(Actions.delay(0.4f, addEnemyAction(SpawnConfiguration(enemy, 5))))
            }).toSequence()

            val action3 = Actions.delay(3.6f, Actions.parallel(
                addEnemyAction(SpawnConfiguration(SandyEnemy(40f), 4)),
                Actions.delay(0.4f, addEnemyAction(SpawnConfiguration(SandyEnemy(60f), 3))),
                Actions.delay(1f, addEnemyAction(SpawnConfiguration(SandyEnemy(50f), 7))),
            ))

            addActionToSequence(Actions.parallel(sneakySandyAction, actions1, actions2, action3, actions4))
        }
    }
}