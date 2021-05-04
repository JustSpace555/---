package lab3.intergration5

import kotlin.math.abs
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
	private const val exactValue = -0.1647401421684573

	private fun rectangleMethod(xList: List<Double>, step: Double): Double =
			xList.windowed(2).sumOf { y(it.sum() / 2) } * step

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

	private fun Dot.methodsError(step: Double): Triple<Double, Double, Double> = Triple(
			rectangleMethod + (calculate(step / 2).last().rectangleMethod - rectangleMethod) / 0.75,
			trapezoidMethod + (calculate(step / 2).last().trapezoidMethod - trapezoidMethod) / 0.75,
			simpsonMethod + (calculate(step / 2).last().simpsonMethod - simpsonMethod) / (1 - 0.5.pow(4))
		)

	operator fun invoke() {
		println("Integral with step $h1:")
		val h1Answer = calculate(h1)
		println(h1Answer.joinToString(separator = "\n", postfix = "\n") { it.toString() })
		println(h1Answer.last().methodsError(h1).toList().map { abs(it - exactValue) })

		println()

		println("Integral with step $h2:")
		val h2Answer = calculate(h2)
		println(h2Answer.joinToString(separator = "\n", postfix = "\n") { it.toString() })
		println(h2Answer.last().methodsError(h2).toList().map { abs(it - exactValue) })
		println("-----------------------------------------------------------------------------")
	}
}