package gaia.managers.fonts

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.odencave.assets.Assets
import gaia.managers.MegaManagers

class FontManager : MegaManagers.Manager {
    lateinit var defaultFont: BitmapFont

    override fun init() {
        val generator = FreeTypeFontGenerator(Gdx.files.internal(Assets.Fonts.font1))
        val params = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            size = 12
            color = Color.WHITE
        }

        defaultFont = generator.generateFont(params)

        generator.dispose()
    }
}
