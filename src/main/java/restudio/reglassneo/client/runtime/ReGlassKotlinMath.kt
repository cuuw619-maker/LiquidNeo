package restudio.reglassneo.client.runtime

import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

/** Small allocation-free math helpers shared by the Java renderer. */
object ReGlassKotlinMath {
    @JvmStatic
    fun smoothingFactor(dt: Double, response: Double = 0.15): Float =
        (1.0 - exp(-dt / response)).toFloat()

    @JvmStatic
    fun timeSeconds(nanos: Long): Float =
        ((nanos * 1.0e-9) % 100000.0).toFloat()

    @JvmStatic
    fun highlightPulse(time: Float): Float =
        0.82f + 0.18f * sin(time * 1.6f)

    @JvmStatic
    fun highlightDirection(time: Float, x: Float, halfWidth: Float, angle: Float): Float =
        0.78f + 0.22f * cos(x / maxOf(halfWidth, 1.0f) + time * 0.55f + angle)
}
