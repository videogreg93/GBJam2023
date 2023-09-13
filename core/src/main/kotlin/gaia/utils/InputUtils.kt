package gaia.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor

fun InputProcessor.addAsInput() {
    val inputProcessor = Gdx.input.inputProcessor
    val multiplexer = InputMultiplexer(this, inputProcessor)
    Gdx.input.inputProcessor = multiplexer
}

fun InputProcessor.removeAsInput() {
    (Gdx.input.inputProcessor as? InputMultiplexer)?.removeProcessor(this)
        ?: ktx.log.error { "Could not remove this input processor. Are you sure you added it before?" }
}
