package gaia.utils

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import gaia.base.BaseActor

class FloatLerpAction(start: Float, end: Float, private val callback: (Float) -> Unit) : FloatAction(start, end) {
    override fun act(delta: Float): Boolean {
        val complete = super.act(delta)
        callback.invoke(value)
        return complete
    }

    companion object {
        fun createLerpAction(
            start: Float,
            end: Float,
            duration: Float,
            interpolation: Interpolation,
            callback: (Float) -> Unit
        ): FloatLerpAction {
            return FloatLerpAction(start, end, callback).apply {
                this.duration = duration
                this.interpolation = interpolation
            }
        }
    }
}

fun delayAction(duration: Float, runnable: () -> Unit): DelayAction {
    return Actions.delay(duration, Actions.run(runnable))
}

fun List<Action>.toSequence() = Actions.sequence(*this.toTypedArray())

val DelayAction.remainingTime: Float
    get() = duration - time

val Action.asActor: BaseActor
    get() = actor as BaseActor

object ActorAction {
    fun removeFromCrew(): Action {
        return Actions.action(RunnableAction::class.java).apply {
            setRunnable {
                asActor.removeFromCrew()
            }
        }
    }
}
