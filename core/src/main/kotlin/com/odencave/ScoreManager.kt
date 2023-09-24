package com.odencave

import com.badlogic.gdx.Gdx
import com.odencave.entities.player.Player
import com.odencave.events.EnemyDestroyedEvent
import com.odencave.models.ShipUpgrade
import gaia.managers.MegaManagers
import gaia.managers.events.EventListener
import kotlin.math.min

class ScoreManager: MegaManagers.Manager, EventListener<EnemyDestroyedEvent> {
    var currentTotalScore = 0
    var upgradeScore = 0
    val player: Player
        get() = MegaManagers.screenManager.getCurrentScreen()!!.crew.getAllOf<Player>().first()
    private val upgrades = listOf(ShipUpgrade.Upgrade1, ShipUpgrade.Upgrade2, ShipUpgrade.Upgrade3)
    private var currentUpgradeIndex = 0
        set(value) {
            field = if (value <= 0) 0 else min(upgrades.size - 1, value)
        }
    val nextUpgrade
        get() = upgrades[currentUpgradeIndex]
    private val previousUpgrade: ShipUpgrade?
        get() = upgrades.getOrNull(currentUpgradeIndex - 1)

    override fun init() {
        currentTotalScore = 0
        MegaManagers.eventManager.subscribeTo<EnemyDestroyedEvent>(this)
    }

    fun downgrade() {
        upgradeScore = 0
        currentUpgradeIndex--
        player.downgradeShip(previousUpgrade)
    }

    override fun onEvent(event: EnemyDestroyedEvent) {
        currentTotalScore += event.enemy.scoreValue
        upgradeScore += event.enemy.scoreValue
        Gdx.app.log("SCOREMANAGER", currentTotalScore.toString())
        if (upgradeScore >= nextUpgrade.scoreThreshold && player.shipUpgrade != ShipUpgrade.Upgrade3) {
            // Upgrade ship
            player.upgradeShip(nextUpgrade)
            currentUpgradeIndex++
            upgradeScore = 0
        }
    }

    fun resetScore() {
        currentTotalScore = 0
        upgradeScore = 0
    }

    companion object {
        const val SCORE_FOR_SECRET_LEVEL = 66600
    }
}
