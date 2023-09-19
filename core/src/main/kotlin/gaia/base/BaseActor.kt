package gaia.base

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import ktx.actors.alpha
import ktx.graphics.copy

/**
 * Represents anything that is to be included in a scene. While more often than not these are visible
 * objects, they can also be textureless and only serve as background workers.
 */
open class BaseActor(texture: Texture? = null, x: Float = 0f, y: Float = 0f) : Actor() {
    var crew: Crew? = null
    var sprite: Sprite? = null

    /**
     * If true, [getWidth] and [getHeight] will always return values based on the sprite's texture rather than
     * returning values based on [currentAnimation]. False by default.
     */
    var ignoreAnimationSize = false
    var hangOnLastFrame = false
    var currentAnimation: Animation<TextureRegion>? = null
    var currentAnimationTime = 0f

    // Force grid position despite actual position
    var hardCodedGridX: Float? = null

    // Force grid position despite actual position
    var hardCodedGridY: Float? = null

    val children = ArrayList<BaseActor>()

    // For doing special effects
    var shader: ShaderProgram? = null
    var isFocused: Boolean = true
        set(value) {
            field = value
            color = if (value) {
                Color.WHITE
            } else {
                UNFOCUSED_COLOR
            }
        }
    var shouldDraw = true
    var shouldAct = true
    var centerPoint: Vector2 = Vector2(0f, 0f)

    // Lower = drawn first
    var drawIndex: Int = 100
        set(value) {
            if (field != value) {
                // Remove and add to crew to update draw index sorting
                crew?.let {
                    removeFromCrew()
                    it.addMember(this)
                }
            }
            field = value
        }

    open val visualX: Float
        get() = x

    open val visualY: Float
        get() = y


    /**
     * Used to slow down or speed up an actor. Higher means going faster
     */
    var speedMultiplier = 1f

    init {
        this.setPosition(x, y)
        if (texture != null) {
            sprite = Sprite(texture).apply {
                setPosition(x, y)
            }
            this.setSize(texture.width.toFloat(), texture.height.toFloat())
        }
    }

    /**
     * This is where an actor can proceed with logic. You must call super or else sprite won't update
     * correctly.
     */
    override fun act(delta: Float) {
        centerPoint.set(
            x + width / 2f,
            y + height / 2f
        )
        setAbsoluteOrigin(centerPoint.x, centerPoint.y)
        currentAnimation?.let {
            currentAnimationTime += delta
        }
        super.act(delta)
        updateSprite()
        // Update children based on parent
        children.forEach {
            it.shouldDraw = shouldDraw
        }
    }

    override fun setPosition(x: Float, y: Float) {
        super.setPosition(x, y)
        updateSprite()
    }

    override fun moveBy(x: Float, y: Float) {
        super.moveBy(x, y)
        updateSprite()
    }

    open fun onAddedToCrew(crew: Crew) {
        crew.addMembers(*children.toTypedArray())
    }

    open fun onRemovedFromCrew(crew: Crew) {
        crew.removeMembers(*children.toTypedArray())
    }

    /**
     * This is where an actor can draw itself on the screen
     */
    override fun draw(batch: Batch, parentAlpha: Float) {
        if (currentAnimation != null) {
            currentAnimation?.let {
                val oldColor = batch.color.copy()
                val tempShader = batch.shader
                batch.shader = shader
                batch.color = color
                val region = it.getKeyFrame(currentAnimationTime)
                // TODO big change, revert if animations are fucked
                batch.draw(
                    region,
                    visualX,
                    visualY,
                    originX,
                    originY,
                    region.regionWidth.toFloat(),
                    region.regionHeight.toFloat(),
                    scaleX,
                    scaleY,
                    rotation
                )
                batch.shader = tempShader
                batch.color = oldColor
                if (it.playMode == Animation.PlayMode.NORMAL && it.isAnimationFinished(currentAnimationTime)) {
                    if (hangOnLastFrame) {
                        currentAnimationTime -= it.frameDuration / 2f
                    } else {
                        currentAnimation = null
                        currentAnimationTime = 0f
                    }
                }
            }
        } else {
            val oldShader = batch.shader
            if (shader != null) {
                batch.shader = shader
            }
            sprite?.draw(batch)
            batch.shader = oldShader
        }
    }

    internal open fun updateSprite() {
        sprite?.setPosition(visualX, visualY)
        sprite?.rotation = rotation
        sprite?.setOrigin(originX, originY)
        sprite?.setSize(width, height)
        sprite?.setScale(scaleX, scaleY)
        sprite?.color = if (!isFocused) {
            UNFOCUSED_COLOR
        } else {
            this.color
        }
        sprite?.setAlpha(alpha)
    }

    fun setAbsoluteOrigin(posX: Float, posY: Float) {
        setOrigin(posX - x, posY - y)
    }

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        return if (x >= this.x && x < width + this.x && y >= this.y && y < height + this.y) this else null
    }

    open fun center() {
        val x = -width / 2
        val y = -height / 2
        setPosition(x, y)
    }

    /**
     * Return the position this actor would need to be centered
     */
    fun getPositionToCenterOn(): Pair<Float, Float> {
        val oldPosition = x to y
        center()
        val centerPosition = x to y
        return centerPosition.also {
            setPosition(oldPosition.first, oldPosition.second)
        }
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BaseActor) return false
        return this.hashCode() == other.hashCode()
    }

    fun removeFromCrew() {
        crew?.removeMember(this)
    }

    open fun centerOn(other: BaseActor) {
        centerOn(other.x, other.y, other.width, other.height)
    }

    /**
     * Return the position this actor would need to be centered
     */
    fun getPositionToCenterOn(other: BaseActor): Pair<Float, Float> {
        val oldPosition = x to y
        centerOn(other)
        val newPosition = x to y
        return newPosition.also {
            setPosition(oldPosition.first, oldPosition.second)
        }
    }

    fun centerOn(x: Float, y: Float, otherWidth: Float, otherHeight: Float) {
        val newX = x + (otherWidth - width) / 2
        val newY = y + (otherHeight - height) / 2
        setPosition(newX, newY)
    }

    override fun getWidth(): Float {
        return if (ignoreAnimationSize) {
            sprite?.width ?: super.getWidth()
        } else {
            currentAnimation?.getKeyFrame(currentAnimationTime)?.regionWidth?.toFloat()
                ?: sprite?.width ?: super.getWidth()
        }
    }

    override fun getHeight(): Float {
        return if (ignoreAnimationSize) {
            return sprite?.height ?: super.getHeight()
        } else {
            currentAnimation?.getKeyFrame(currentAnimationTime)?.regionHeight?.toFloat()
                ?: return sprite?.height ?: super.getHeight()
        }
    }

    /**
     * Determines wether an actor contains another actor within it. Can be used to keep things from
     * going out of bounds
     * @return True if this actor contains the other actor
     */
    fun contains(other: BaseActor): Boolean {
        return (other.x >= x && other.x + other.width <= x + width && other.y >= y && other.y + other.height <= y + height)
    }

    /**
     * @return flicker duration
     */
    fun addFlickerAction(flickerInterval: Float = 1f, repetitions: Int = RepeatAction.FOREVER): Float {
        alpha = 1f
        val hideAction = Actions.delay(flickerInterval, Actions.run {
            shouldDraw = false
        })
        val showAction = Actions.delay(flickerInterval, Actions.run {
            shouldDraw = true
        })
        val sequence = Actions.sequence(hideAction, showAction)
        val repeatAction = Actions.repeat(repetitions, sequence)
        addAction(repeatAction)
        return flickerInterval * 2 * repetitions
    }

    fun addFlickerRedAction(flickerInterval: Float = 1f, repetitions: Int = RepeatAction.FOREVER) {
        val hideAction = Actions.delay(flickerInterval, Actions.run {
            color = Color.FIREBRICK
        })
        val showAction = Actions.delay(flickerInterval, Actions.run {
            color = Color.WHITE
        })
        val sequence = Actions.sequence(hideAction, showAction)
        val repeatAction = Actions.repeat(repetitions, sequence)
        addAction(repeatAction)
    }

    fun removeAllActions() {
        actions.toList().forEach {
            removeAction(it)
        }
    }

    fun rectangle(): Rectangle {
        return Rectangle(x, y, width, height)
    }

    fun setTexture(texture: Texture) {
        sprite?.let {
            it.texture = texture
        } ?: ktx.log.error { "Sprite is null, cannot assign new texture for $name" }
    }

    fun pos(): Vector2 = Vector2(x,y)

    companion object {
        val UNFOCUSED_COLOR = Color.GRAY
        const val MIN_GRID_X = 1
        const val MIN_ENEMY_GRID_X = 4
        const val MAX_GRID_X = 6
        const val MAX_FRIENDLY_GRID_X = 3
        const val MIN_GRID_Y = 2
        const val MAX_GRID_Y = 4
    }

}
