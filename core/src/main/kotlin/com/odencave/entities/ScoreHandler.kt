package com.odencave.entities

import com.odencave.ScoreManager
import gaia.managers.MegaManagers
import gaia.ui.generic.Label

class ScoreHandler : Label("0", MegaManagers.fontManager.defaultFont) {
    private val manager by lazy { MegaManagers.getManager<ScoreManager>() }

    override fun act(delta: Float) {
        super.act(delta)
        text = manager.currentTotalScore.toString()
    }
}
