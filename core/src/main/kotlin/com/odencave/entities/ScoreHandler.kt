package com.odencave.entities

import com.odencave.events.EnemyDestroyedEvent
import gaia.base.BaseActor
import gaia.base.Crew
import gaia.managers.MegaManagers
import gaia.managers.events.EventListener
import gaia.ui.generic.Label

class ScoreHandler: Label("", MegaManagers.fontManager.defaultFont), EventListener<EnemyDestroyedEvent> {
    var currentScore = 0

    override fun onAddedToCrew(crew: Crew) {
        super.onAddedToCrew(crew)
        MegaManagers.eventManager.subscribeTo<EnemyDestroyedEvent>(this)
    }

    override fun onRemovedFromCrew(crew: Crew) {
        super.onRemovedFromCrew(crew)
        MegaManagers.eventManager.unsubscribe<EnemyDestroyedEvent>(this)
    }

    override fun act(delta: Float) {
        super.act(delta)
        text = currentScore.toString()
    }

    override fun onEvent(event: EnemyDestroyedEvent) {
        currentScore += event.enemy.scoreValue
    }
}
