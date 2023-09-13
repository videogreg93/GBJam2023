package gaia.managers

import ktx.log.info
import kotlin.random.Random

class RandomManager(var random: Random = Random(Random.nextInt())) {

    fun setSeed(seed: Int) {
        random = Random(seed)
        info { "Set random seed to new seed $seed" }
    }

    /**
     * Returns a random value based on [variance]
     */
    fun valueWithVariance(initialValue: Float, variance: Float): Float {
        return (initialValue - variance) + (variance * 2 * random.nextFloat())
    }

}
