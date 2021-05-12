package lab4

import kotlin.math.abs
import kotlin.math.ln

object Lab4_2 {

	private val accurateY: (Double) -> Double = { x -> 2 + x + 2 * x * ln(abs(x)) }

	private fun shootingMethod() {
		val y0 = accurateY(1.0)
		val y1 = accurateY(3.0)
	}
}