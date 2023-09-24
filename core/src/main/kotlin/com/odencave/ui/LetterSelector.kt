package com.odencave.ui

import gaia.managers.MegaManagers
import gaia.ui.generic.Label

class LetterSelector: Label("A", MegaManagers.fontManager.defaultFont) {

    fun moveUp() {
        updateText((text.first().code + 1).toChar())
    }

    fun moveDown() {
        updateText((text.first().code - 1).toChar())
    }

    fun getChar() = text.first()

    private fun updateText(newChar: Char) {
        var newChar1 = newChar
        if (newChar1.code > 90) {
            newChar1 = (48).toChar()
        } else if (newChar1.code < 48) {
            newChar1 = 90.toChar()
        }
        text = newChar1.toString()
    }

    companion object {
        private val validValues = (48..90)
    }
}
