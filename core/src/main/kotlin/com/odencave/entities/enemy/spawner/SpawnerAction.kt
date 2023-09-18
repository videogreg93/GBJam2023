package com.odencave.entities.enemy.spawner

import com.odencave.i18n.entities.enemy.spawner.SpawnConfiguration

sealed class SpawnerAction {
    class SpawnEnemy(val configs: List<SpawnConfiguration>) : SpawnerAction()

    class Wait(val delay: Float) : SpawnerAction()
}
