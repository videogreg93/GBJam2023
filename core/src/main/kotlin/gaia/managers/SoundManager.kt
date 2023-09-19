package gaia.managers

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import kotlinx.coroutines.*

class SoundManager {
    private var fadeJobs = HashMap<Music, Job>()
    var currentMusic: Music? = null

    fun init() {

        //loadVolumes()
        // TODO make sure this isn't overridden at some point by the sharedPrefs
        if (false) {
            CHANNEL.MUSIC.volume = 0f
            CHANNEL.SFX.volume = 0f
            CHANNEL.OTHER.volume = 0f
            //saveVolumes()
        }

    }

    fun playMusicWithIntro(music: Music, intro: Music, isLooping: Boolean = true) {
        currentMusic?.pause()
        currentMusic = intro
        intro.volume = CHANNEL.MUSIC.volume
        music.volume = CHANNEL.MUSIC.volume
        try {
            intro.play()
            intro.setOnCompletionListener {
                playMusic(music, isLooping, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playMusic(
        music: Music,
        isLooping: Boolean = true,
        fadeIn: Boolean = true,
        position: Float? = null,
        fadeInDelay: Long = 20L
    ) {
        if (music.isPlaying)
            return
        music.isLooping = isLooping
        currentMusic?.pause()
        currentMusic = music
        if (fadeIn) {
            music.volume = 0f
            fadeJobs[music]?.cancel()
            fadeJobs[music] = CoroutineScope(Dispatchers.Default).launch {
                while (music.volume != CHANNEL.MUSIC.volume) {
                    music.volume = (music.volume + 0.01f).coerceAtMost(CHANNEL.MUSIC.volume)
                    delay(fadeInDelay)
                }
            }
        } else {
            music.volume = CHANNEL.MUSIC.volume
        }
        try {
            music.play()
            position?.let {
                music.position = it
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun pauseMusic(music: Music) {
        music.pause()
    }

    fun stopCurrentMusic() {
        currentMusic?.let {
            stopMusic(it)
        }
    }

    fun pauseCurrentMusic() {
        currentMusic?.let {
            pauseMusic(it)
        }
    }

    fun stopMusic(music: Music, fadeOut: Boolean = true) {
        if (fadeOut) {
            fadeJobs[music]?.cancel()
            CoroutineScope(Dispatchers.Default).launch {
                while (music.volume != 0f) {
                    music.volume = (music.volume - 0.01f).coerceAtLeast(0f)
                    delay(20)
                }
                music.stop()
                music.volume = CHANNEL.MUSIC.volume
            }
        } else {
            music.stop()
        }
    }

    fun play(sound: Sound, channel: CHANNEL = CHANNEL.OTHER, volumeModifier: Float = 0f, pitch: Float = 1f): Long {
        return sound.play(channel.volume + volumeModifier, pitch, 0f)
    }

    fun playRandomSFX(vararg sounds: Sound): Long {
        return play(sounds.random(MegaManagers.randomManager.random))
    }

    fun playSFX(sound: Sound, volumeModifier: Float = 0f): Long {
        return play(sound, CHANNEL.SFX, volumeModifier)
    }

    fun playSFXRandomPitch(sound: Sound, volumeModifier: Float = 0f): Long {
        val pitch = listOf(0.8f, 0.9f, 1f, 1.1f, 1.2f).random()
        return play(sound, CHANNEL.SFX, volumeModifier, pitch)
    }

    fun stopSFX(sound: Sound, id: Long? = null) {
        if (id != null) {
            sound.stop(id)
        } else {
            sound.stop()
        }
    }

    fun loopSFX(sound: Sound, volumeModifier: Float = 0f): Long {
        return sound.loop((CHANNEL.SFX.volume + volumeModifier).coerceIn(0f, 1f))
    }

    fun dispose() {

    }

    fun setVolume(channel: CHANNEL, volume: Float) {
        channel.volume = volume
        if (channel == CHANNEL.MUSIC) {
            currentMusic?.volume = volume
        }
    }

    /**
     * Called when the game is paused, so we can lower music volume
     */
    fun setPausedVolume() {
        currentMusic?.volume = CHANNEL.MUSIC.volume * PAUSED_VOLUME_MODIFIER
    }

    /**
     * Called when the game is unpaused, so we can set music volume back to normal
     */
    fun setUnpausedVolume() {
        currentMusic?.volume = CHANNEL.MUSIC.volume
    }

    sealed class CHANNEL(var volume: Float) {
        object SFX : CHANNEL(0.8f)
        object MUSIC : CHANNEL(0.5f)
        object OTHER : CHANNEL(1f)
    }

    companion object {

        private const val PAUSED_VOLUME_MODIFIER = 0.35f
    }
}
