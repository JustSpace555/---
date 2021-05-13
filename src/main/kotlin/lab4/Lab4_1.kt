package lab4

import kotlin.math.*

class Lab4_1(
	private val a: Int = 0,
	private val b: Int = 1,
	private val h: Double = 0.1,
	private val y0: Double = 1.0,
	private val yStroke0: Double = 1.0,
	private val accurateY: (Double) -> Double = { x -> x - x.pow(2) + 1 },
	private val koshiQuestion: (Double, Double, Double) -> Double = { x, y, z ->
		(2 * x * z - 2 * y) / (x.pow(2) + 1)
	}
) {

	companion object {
		fun rrr(h: Double, method: (Double) -> List<Double>): List<Double> {
			val methodNorm = method(h)
			val methodHalf = method(h / 2)
			return methodNorm
				.zip(methodHalf.filterIndexed { i, _ -> i % 2 == 0 })
				.map { it.first + (it.second - it.first) / 0.75 }
		}
	}

	private fun euler(h: Double = this.h): List<Double> {
		val x = List( ((b - a) / h).toInt() + 1 ) { index -> a + index * h }
		val y = MutableList(x.size) { y0 }
		val z = MutableList(x.size) { yStroke0 }

		for (i in 1 .. x.lastIndex) {
			z[i] = z[i - 1] + h * koshiQuestion(x[i - 1], y[i - 1], z[i - 1])
			y[i] = y[i - 1] + h * z[i - 1]
		}

		return y
	}

	private fun runge(h: Double = this.h): Pair<List<Double>, List<Double>> {
		val k = MutableList(4) { 0.0 }
		val l = MutableList(4) { 0.0 }
		val x = List( ((b - a) / h).toInt() + 1 ) { index -> a + index * h }
		val y = MutableList(x.size) { y0 }
		val z = MutableList(x.size) { yStroke0 }

		val klFun: List<Double>.() -> Double = { (this[0] + 2 * this[1] + 2 * this[2] + this[3]) / 6 }

		for (i in 1 .. x.lastIndex) {
			for (j in 1 .. k.lastIndex) {
				k[0] = h * z[i - 1]
				l[0] = h * koshiQuestion(x[i - 1], y[i - 1], z[i - 1])
				k[j] = h * (z[i - 1] + l[j - 1] / 2)
				l[j] = h * koshiQuestion(x[i - 1] + h / 2, y[i - 1] + k[j - 1] / 2, z[i - 1] + l[j - 1] / 2)
			}
			y[i] = y[i - 1] + k.klFun()
			z[i] = z[i - 1] + l.klFun()
		}

		return Pair(y, z)
	}

	fun rungeY(h: Double = this.h) = runge(h).first
	fun rungeZ(h: Double = this.h) = runge(h).second

	private fun adams(h: Double = this.h): List<Double> {

		val x = List( ((b - a) / h).toInt() + 1 ) { index -> a + index * h }
		val y = rungeY(h).toMutableList()
		val z = rungeZ(h).toMutableList()

		val koshiQI: (Int) -> Double = { i -> koshiQuestion(x[i], y[i], z[i]) }

		for (i in 4 .. x.lastIndex) {
			z[i] = z[i - 1] + h / 24 * (
					55 * koshiQI(i - 1) - 59 * koshiQI(i - 2) + 37 * koshiQI(i - 3) - 9 * koshiQI(i - 4)
					)
			y[i] = y[i - 1] + h * z[i - 1]
		}
		return y
	}

	operator fun invoke() {
		val x = List( ((b - a) / h).toInt() + 1 ) { index -> a + index * h }

		println("X: $x")
		println("Accurate y: ${x.map { accurateY(it) }}")
		println("Euler method: ${euler()}")
		println("Runge method: ${rungeY()}")
		println("Adams method: ${adams()}")
		println()

		val eulerR = rrr(h, ::euler)
		val rungeR = rrr(h, ::rungeY)
		val adamsR = rrr(h, ::adams)
		println("Runge Rombert Richrdson method")
		println("Euler method: $eulerR")
		println("Runge method: $rungeR")
		println("Adams method: $adamsR")
		println()

		val delta: (Int, Double) -> Double = { i, d -> abs(d - accurateY(x[i])) }
		println("Delta")
		println("Euler: ${eulerR.mapIndexed(delta)}")
		println("Runge: ${rungeR.mapIndexed(delta)}")
		println("Adams: ${adamsR.mapIndexed(delta)}")
	}
}