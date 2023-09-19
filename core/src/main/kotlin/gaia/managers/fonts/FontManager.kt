package gaia.managers.fonts

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.odencave.assets.Assets
import gaia.managers.MegaManagers

class FontManager : MegaManagers.Manager {
    lateinit var defaultFont: BitmapFont
    lateinit var titleFont: BitmapFont

    override fun init() {
        val generator = FreeTypeFontGenerator(Gdx.files.internal(Assets.Fonts.font1))
        val params = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            size = 12
            color = Color.WHITE
        }
        defaultFont = generator.generateFont(params)

        val generator2 = FreeTypeFontGenerator(Gdx.files.internal(Assets.Fonts.font1Bold))
        val params2 = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            size = 12
            color = Color.valueOf("202020")
        }
        titleFont = generator.generateFont(params2)


        generator.dispose()
        generator2.dispose()
    }
}
