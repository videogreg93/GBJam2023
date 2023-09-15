package gaia.managers

import gaia.ui.Modal


class ModalManager {

    private var modalListener: ModalListener? = null

    fun setModalListener(listener: ModalListener) {
        modalListener = listener
    }

    fun addModal(modal: Modal) {
        modalListener?.addModal(modal)
    }

    fun removeModal(modal: Modal) {
        modalListener?.removeModal(modal)
    }

    fun removeAllModals() {
        while (currentModal() != null) {
            currentModal()?.let {
                removeModal(it)
            }
        }
    }

    fun currentModal(): Modal? {
        return modalListener?.currentModal
    }

    interface ModalListener {
        val currentModal: Modal?
        fun addModal(modal: Modal)
        fun removeModal(modal: Modal)
    }
}
