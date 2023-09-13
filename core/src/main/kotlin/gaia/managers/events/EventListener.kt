package gaia.managers.events

interface EventListener<in T: EventInstance> {
    fun onEvent(event: T)
}
