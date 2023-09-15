package com.odencave.i18n.entities.enemy

import com.badlogic.gdx.scenes.scene2d.Action
import com.odencave.entities.enemy.Enemy

class MoveStraightAction : Action() {
    override fun act(delta: Float): Boolean {
        val enemy = (this.actor as Enemy)
        enemy.x -= enemy.currentMoveSpeed * delta
        return true
    }
}
