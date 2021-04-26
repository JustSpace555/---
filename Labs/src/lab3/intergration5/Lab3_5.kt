package lab3.intergration5

import kotlin.math.pow

object Lab3_5 {

	private val y: (Double) -> Double = { x ->
//		x / (x.pow(3) + 8)
		x / (3 * x + 4).pow(2)
	}

	private const val X0 = -1
	private const val Xk = 1
	private const val h1 = 0.5
	private const val h2 = 0.25

	private fun rectangleMethod(xList: List<Double>, step: Double): Double =
			xList.windowed(2).map { y(it.sum() / 2) }.sum() * step

	private fun trapezoidMethod(yList: List<Double>, step: Double): Double = (
			yList.first() / 2 + yList.slice(1 until yList.lastIndex).sum() + yList.last() / 2
			) * step

	private fun simpsonMethod(yList: List<Double>, step: Double): Double =
			yList.mapIndexed { index, d ->
				when {
					index == 0 || index == yList.lastIndex -> d
					index % 2 == 1 -> 4 * d
					else -> 2 * d
				}
			}.sum() * (step / 3)

	private fun calculate(step: Double): List<Dot> {

		val output = mutableListOf(Dot(
				i = 0,
				xi = X0.toDouble(),
				yi = y(X0.toDouble()),
				rectangleMethod = 0.0,
				trapezoidMethod = 0.0,
				simpsonMethod = 0.0
		))

		var iter = X0 + step
		var i = 0
		while (iter <= Xk) {
			val fx = y(iter)
			output.add(Dot(
					i = ++i,
					xi = iter,
					yi = fx,
					rectangleMethod = rectangleMethod(output.map { it.xi } + iter, step),
					trapezoidMethod = trapezoidMethod(output.map { it.yi } + fx, step),
					simpsonMethod = simpsonMethod(output.map { it.yi } + fx, step)
			))
			iter += step
		}
		return output
	}

	operator fun invoke() {
		println("Integral with step $h1:")
		println(calculate(h1).joinToString(separator = "\n", postfix = "\n") { it.toString() })
		println("Integral with step $h2:")
		println(calculate(h2).joinToString(separator = "\n", postfix = "\n") { it.toString() })
		println("-------------------------------------------------------------------------ё---")
	}
}