package gaia.ui

import gaia.managers.MegaManagers

abstract class Modal(title: String, val showOverlay: Boolean = true, val pauseMainScreen: Boolean = false) : BasicScreen(title) {

    var onDismiss: (() -> Unit)? = null

    open fun dismiss() {
        onDismiss?.invoke()
        MegaManagers.modalManager.removeModal(this)
    }

    /**
     * Shows the modal on screen
     */
    fun inflate(): Modal {
        MegaManagers.modalManager.addModal(this)
        return this
    }
}
