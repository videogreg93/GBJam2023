package gaia.managers.events

abstract class EventInstance {
    val identifier: String
        get() = this::class.simpleName ?: error("Cannot use abstract classes with event instance")
}
