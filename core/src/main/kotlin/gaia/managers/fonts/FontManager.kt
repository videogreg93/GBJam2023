package gaia.managers.fonts

//import gaia.assets.Assets
import com.badlogic.gdx.graphics.g2d.BitmapFont
import gaia.managers.MegaManagers

class FontManager : MegaManagers.Manager {
    lateinit var defaultFont: BitmapFont

    lateinit var chooseCharacterFont: BitmapFont

    override fun init() {
//        val generator = FreeTypeFontGenerator(Gdx.files.internal(Assets.Fonts.grand9k))
//        val params = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
//            size = 32
//            color = Color.BLACK
//        }
//
//        defaultFont = generator.generateFont(params)


    }

    companion object {
        const val FONT_FOLDER = "fonts"
        const val CARD_FONT = "m5x7.ttf"
        const val SPELL_FONT = "grand9k.ttf"
    }
}
