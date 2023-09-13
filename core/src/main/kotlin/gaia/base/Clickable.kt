package gaia.base

interface Clickable {
    /**
     * @return true to consume the click
     */
    fun onClick(): Boolean
}
