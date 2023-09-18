package com.odencave.events

import com.odencave.entities.enemy.Enemy
import gaia.managers.events.EventInstance

class EnemyDestroyedEvent(val enemy: Enemy): EventInstance() {
}
