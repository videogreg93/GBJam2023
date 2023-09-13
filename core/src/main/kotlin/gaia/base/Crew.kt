package gaia.base

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.graphics.use
import kotlin.reflect.KClass

/**
 * Custom implementation of a scene, it groups actors together
 */
class Crew(val batch: SpriteBatch, private val camera: Camera = OrthographicCamera()) : BaseActor() {
    var members = ArrayList<BaseActor>()
    var alpha = 1f

    fun addMember(member: BaseActor) {
        if (!members.contains(member)) {
            members.add(member)
            member.crew = this
            member.onAddedToCrew(this)
            members.sortByDescending { it.drawIndex }
        }
    }

    fun addMembers(vararg others: BaseActor) {
        others.forEach {
            addMember(it)
        }
    }

    fun addMembers(others: List<BaseActor>) = addMembers(*others.toTypedArray())

    fun removeMember(member: BaseActor): Boolean {
        member.onRemovedFromCrew(this)
        member.crew = null
        return members.remove(member)
    }

    fun removeMembers(vararg others: BaseActor) {
        others.forEach {
            removeMember(it)
        }
    }

    fun removeMembers(others: List<BaseActor>) = removeMembers(*others.toTypedArray())

    fun removeAllMembers() {
        val temp = ArrayList(members)
        temp.forEach {
            removeMember(it)
        }
    }

    inline fun <reified T : BaseActor> removeAllOf() {
        members.filterIsInstance<T>().forEach {
            it.removeFromCrew()
        }
    }

    /**
     * Calls act on each actor, modifying [delta] by that actor's speedMultiplier
     */
    override fun act(delta: Float) {
        super.act(delta * speedMultiplier)
        for (i in 0 until members.size) {
            members.getOrNull(i)?.let { member ->
                if (member.shouldAct) {
                    member.act(delta * speedMultiplier * member.speedMultiplier)
                }
            }
        }
    }

    /**
     * Calls act on specific actors of type [T]
     */
    fun actOn(type: KClass<*>, delta: Float) {
        super.act(delta)
        for (i in 0 until members.size) {
            members.getOrNull(i)?.let { member ->
                if (member.shouldAct && type.isInstance(member)) {
                    member.act(delta * member.speedMultiplier)
                }

            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        render()
    }

    fun render() {
        camera.update()
        batch.use(camera) {
            for (i in 0 until members.size) {
                members.getOrNull(i)?.let {member ->
                    if (member.shouldDraw) {
                        member.draw(batch, alpha)
                    }
                }
            }
        }
    }


}
