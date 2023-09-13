package gaia.base

import com.badlogic.gdx.graphics.Texture

class Background(texture: Texture? = null): BaseActor(texture) {

    init {
        setPosition(-width/2, -height/2)
    }
}
