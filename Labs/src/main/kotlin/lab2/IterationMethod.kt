package lab2

import lab2.extension.minus
import kotlin.math.*

object IterationMethod {

	fun exercise1(x0: Double, epsilon: Double, q: Double): Pair<Double, Int> {
		var prevX: Double
		var x = x0

		var i = 0
		do {
			prevX = x
			x = sqrt(3.0.pow(x) / 5 + 0.2)
			i++
		} while ((q / (1 - q)) * abs(x - prevX) > epsilon)

		return Pair(x, i)
	}

	fun exercise2(x0: Pair<Double, Double>, epsilon: Double, q: Double): Triple<Double, Double, Int> {
		var x = x0.copy()
		var prevX: Pair<Double, Double>
		var i = 0
		do {
			prevX = x.copy()
			x = Pair(3 + cos(x.second), 3 + sin(x.first))
			i++
		} while ((q / (1 - q)) * (x - prevX).toList().map { abs(it) }.maxOrNull()!! > epsilon)

		return Triple(x.first, x.second, i)
	}
}