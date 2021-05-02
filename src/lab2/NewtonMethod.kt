package lab2

import lab2.extension.minus
import kotlin.math.*

internal object NewtonMethod {

	fun exercise1(x0: Double, epsilon: Double): Pair<Double, Int> {

		var x = x0
		var prevX: Double

		val f: (Double) -> Double = { x -> 3.0.pow(x) - 5 * x.pow(2) + 1 }
		val df: (Double) -> Double = { x -> 3.0.pow(x) * ln(3.0) - 10 * x }

		var i = 0
		do {
			prevX = x
			x -= f(x) / df(x)
			i++
		} while (abs(x - prevX) >= epsilon)

		return Pair(x, i)
	}

	fun exercise2(x0: Pair<Double, Double>, epsilon: Double): Triple<Double, Double, Int> {
		var x = x0.copy()
		var prevX: Pair<Double, Double>

		val function1: (Double, Double) -> Double = { x1, x2 -> x1 - cos(x2) - 3 }  // Функция 1
		val function2: (Double, Double) -> Double = { x1, x2 -> x2 - sin(x1) - 3 }  // Функция 2

		val derFun1DerX1 = 1.0                                    // Производная функции 1 по х1
		val derFun1DerX2 = ::sin                                  // Производная функции 1 по x2 (Подавать х2)

		val derFun2DerX1: (Double) -> Double = { x1 -> -cos(x1)}  // Производная функции 2 по х1 (Подавать x1)
		val derFun2DerX2 = 1.0                                    // Производная функции 2 по x2

		// Детерминанты по формулам
		val detA1: (Double, Double) -> Double = { x1, x2 ->
			function1(x1, x2) * derFun2DerX2 - function2(x1, x2) * derFun1DerX2(x2)
		}
		val detA2: (Double, Double) -> Double = { x1, x2 ->
			function2(x1, x2) * derFun1DerX1 - function1(x1, x2) * derFun2DerX1(x1)
		}
		val detJ: (Double, Double) -> Double = { x1, x2 ->
			derFun1DerX1 * derFun2DerX2 - derFun1DerX2(x2) * derFun2DerX1(x1)
		}

		var i = 0
		do {
			prevX = x.copy()
			x -= Pair(
				detA1(x.first, x.second) / detJ(x.first, x.second),
				detA2(x.first, x.second) / detJ(x.first, x.second)
			)
			println(x - prevX)
			i++
		} while ((x - prevX).toList().map { abs(it) }.maxOrNull()!! > epsilon)

		return Triple(x.first, x.second, i)
	}
}