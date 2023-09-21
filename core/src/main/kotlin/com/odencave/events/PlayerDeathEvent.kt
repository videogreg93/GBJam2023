package com.odencave.events

import com.odencave.entities.player.Player
import gaia.managers.events.EventInstance

class PlayerDeathEvent(val player: Player): EventInstance()
