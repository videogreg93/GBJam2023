package com.odencave.screens

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.odencave.SFX
import com.odencave.ScoreManager
import com.odencave.i18n.gaia.base.BackgroundGrid
import com.odencave.models.Leaderboard
import com.odencave.models.LeaderboardEntry
import com.odencave.ui.LetterSelector
import gaia.Globals
import gaia.managers.MegaManagers
import gaia.managers.assets.AssetManager.Companion.get
import gaia.managers.input.ActionListener
import gaia.ui.BasicScreen
import gaia.ui.generic.Label
import gaia.ui.utils.alignTop
import gaia.ui.utils.alignTopToBottomOfLabel

class LeaderboardScreen : BasicScreen("Game Over") {

    private val leaderboardTitleLabel = Label("Leaderboard", MegaManagers.fontManager.defaultFont)
    private val leaderboardLabel by lazy { Label(getLeaderboardText(), MegaManagers.fontManager.defaultFont) }

    private val label1 = LetterSelector()
    private val label2 = LetterSelector()
    private val label3 = LetterSelector()

    private var selectedLabelIndex = 0
        set(value) {
            field = value.coerceIn(0, 2)
        }

    private val selectedLabel: LetterSelector
        get() = when (selectedLabelIndex) {
            0 -> label1
            1 -> label2
            else -> label3
        }


    override fun firstShown() {
        super.firstShown()
        val backgroundGrid = BackgroundGrid(DeathScreen.backgroundAsset.get(), 0, 0)
        backgroundCrew.addMember(backgroundGrid)
        leaderboardTitleLabel.center()
        leaderboardTitleLabel.alignTop(-20f)
        crew.addMember(leaderboardTitleLabel)
        leaderboardLabel.center()
        leaderboardLabel.alignTopToBottomOfLabel(leaderboardTitleLabel, -100f)

        label1.y = -60f
        label1.x -= 20f
        label2.setPosition(label1.x + 15, -60f)
        label3.setPosition(label2.x + 15, -60f)
        crew.addMembers(label1, label2, label3, leaderboardLabel)
    }

    override fun render(delta: Float) {
        super.render(delta)
        label1.shouldDraw = selectedLabelIndex >= 0
        label2.shouldDraw = selectedLabelIndex >= 1
        label3.shouldDraw = selectedLabelIndex >= 2
    }

    fun getLeaderboardText(): String {
        val sb = StringBuilder()
        Leaderboard.getLeaderboardEntries().forEach {
            sb.appendLine("${it.name}: ${it.score}").appendLine()
        }
        return sb.toString()
    }

    override fun onAction(action: ActionListener.InputAction): Boolean {
        when (action) {
            ActionListener.InputAction.UP -> selectedLabel.moveUp()
            ActionListener.InputAction.DOWN -> selectedLabel.moveDown()
            ActionListener.InputAction.START -> {
                MegaManagers.soundManager.playSFX(SFX.select.get())
                if (selectedLabelIndex < 2) {
                    selectedLabelIndex++
                } else {
                    saveEntry()
                }
            }

            else -> return false
        }
        return true
    }

    private fun saveEntry() {
        val name = label1.getChar().toString() + label2.getChar() + label3.getChar()
        val score = MegaManagers.getManager<ScoreManager>().currentTotalScore
        val entry = LeaderboardEntry(name, score)
        Leaderboard.addEntry(entry)
        Globals.resetForNewGame()
        leaderboardLabel.text = getLeaderboardText()
        MegaManagers.inputActionManager.disableAllInputs()
        MegaManagers.screenManager.addGlobalAction(Actions.delay(2f, Actions.run {
            MegaManagers.inputActionManager.enableAllInputs()
            MegaManagers.screenManager.changeScreen(TitleScreen())
        }))
    }
}
