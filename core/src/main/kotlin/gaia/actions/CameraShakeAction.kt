package gaia.actions

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import gaia.managers.MegaManagers
import gaia.managers.context.MainContext
import gaia.utils.FloatLerpAction
import kotlin.random.Random

class CameraShakeAction(
    private val camera: OrthographicCamera,
    _time: Float = 1f,
    private val rumblePower: Float
) : TemporalAction(_time) {

    private var currentPower = 0f
    private var pos = Vector2()
    private var random: Random = MainContext.inject()
    private val initialCameraPosition = Vector3(camera.position)

    override fun update(percent: Float) {
        currentPower = rumblePower * (1f - percent)
        pos.x = (random.nextFloat() - 0.5f) * 2 * currentPower
        pos.y = (random.nextFloat() - 0.5f) * 2 * currentPower

        camera.translate(pos)
    }

    override fun end() {
        val cameraStart = Vector2(camera.position.x, camera.position.y)
        val cameraEnd = initialCameraPosition
        MegaManagers.screenManager.addGlobalAction(
            Actions.parallel(
                makeLerpAction(cameraStart.x, cameraEnd.x, camera, true),
                makeLerpAction(cameraStart.y, cameraEnd.y, camera, false)
            )
        )
        super.finish()
    }

    private fun makeLerpAction(start: Float, end: Float, camera: Camera, isX: Boolean): FloatLerpAction {
        return FloatLerpAction(start, end) {
            if (isX)
                camera.position?.x = it
            else
                camera.position?.y = it
        }.apply {
            duration = 0.1f
            interpolation = Interpolation.linear
        }
    }
}
