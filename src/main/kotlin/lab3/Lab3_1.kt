package lab3

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

internal object Lab3_1 {

	private const val xStar = 1.0

	private val y: (Double) -> Double = { x -> sin(x) + x }

	private fun lagrange() {
		val xiLagrange = listOf(0.0, PI / 6, 2 * PI / 6, 3 * PI / 6)

		val w4: (Double, Int?) -> Double = { x, i ->
			i?.let { index ->
				var times = 1.0
				with (xiLagrange.toMutableList()) {
					removeAt(index)
					forEach { xi -> times *= (x - xi) }
				}
				times
			} ?: (x - xiLagrange[0]) * (x - xiLagrange[1]) * (x - xiLagrange[2]) * (x - xiLagrange[3])
		}

		val polynomial: (Double, List<Double>) -> Double = { x, list ->
			var sum = 0.0
			for (i in 0 .. 3) {
				sum += (y(list[i]) * w4(x, null)) / ((x - xiLagrange[i]) * w4(xiLagrange[i], i))
			}
			sum
		}

		for (i in xiLagrange.indices) {
			val xi = xiLagrange[i]
			val fi = y(xiLagrange[i])
			val w4res = w4(xi, i)
			val del = fi / w4res
			val min = xStar - xi
			println("i = $i, x_i = $xi, f_i = $fi, w' = $w4res, f_i / w'_4(x_i) = $del, X^* - x_i = $min")
		}

		val result = polynomial(xStar, xiLagrange)
		println("Lagrange result: $result")

		val funResult = y(xStar)
		println("Function result: ${y(xStar)}")

		println("The absolute error of interpolation is: ${abs(abs(result) - abs(funResult))}")
	}

	private fun newton() {
		val xiNewton = listOf(0.0, PI / 6, PI / 4, PI / 2)

		fun new(input: List<Double>): Double =
			when (input.size) {
				1 -> y(input.first())
				2 -> (y(input[0]) - y(input[1])) / (input[0] - input[1])
				else -> (new(input.subList(0, input.lastIndex)) - new(input.subList(1, input.size))) /
						(input.first() - input.last())
			}

		val polynomial: (Double) -> Double = { x ->
			var sum = 0.0
			for (i in xiNewton.indices) {
				var times = 1.0
				for (j in 0 until i) times *= x - xiNewton[j]

				sum += times * new(xiNewton.subList(0, i + 1))
			}
			sum
		}

		xiNewton.forEachIndexed { i, d -> println("i = $i, x_$i = $d") }
		xiNewton.forEachIndexed { i, _ ->
			var xi = 0
			xiNewton.windowed(i + 1).forEach loop@{ list ->
				val sb = StringBuffer("f(")
				if (list.size == 1) {
					sb.append("x_${xi++}) = ${new(list)}")
					println(sb.toString())
					return@loop
				}
				for (j in 0 until list.lastIndex) sb.append("x_${xi++}, ")
				xi--
				sb.append("x_${++xi}) = ${new(list)}")
				println(sb.toString())
			}
			xi = 0
		}

		val result = polynomial(xStar)
		println("Newton result: $result")

		val funResult = y(xStar)
		println("Function result: ${y(xStar)}")

		println("The absolute error of interpolation is: ${abs(abs(result) - abs(funResult))}")
	}

	operator fun invoke() {
		println("Exercise 3.1 - Lagrange:")
		lagrange()
		println()
		println("Exercise 3.1 - Newton:")
		newton()
		println("-------------------------------------------------------\n")
	}
}