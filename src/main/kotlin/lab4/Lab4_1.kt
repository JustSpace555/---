package lab4

import kotlin.math.*

object Lab4_1 {

	private const val a = 0
	private const val b = 1
	private const val h = 0.1

	private const val y0 = 1.0
	private const val yStroke0 = 1.0

	private val koshiQuestion: (Double, Double, Double) -> Double = { x, y, z ->
		(2 * x * z - 2 * y) / (x.pow(2) + 1)
	}
	private val accurateY: (Double) -> Double = { x -> x - x.pow(2) + 1 }

	private fun euler(h: Double = this.h): List<Double> {
		val x = List( ((b - a) / h).toInt() + 1 ) { index -> index * h }
		val y = MutableList(x.size) { y0 }
		val z = MutableList(x.size) { yStroke0 }

		for (i in 1 .. x.lastIndex) {
			z[i] = z[i - 1] + h * koshiQuestion(x[i - 1], y[i - 1], z[i - 1])
			y[i] = y[i - 1] + h * z[i - 1]
		}

		return y
	}

	fun runge(h: Double = this.h): Pair<List<Double>, List<Double>> {
		val k = MutableList(4) { 0.0 }
		val l = MutableList(4) { 0.0 }
		val x = List( ((b - a) / h).toInt() + 1 ) { index -> index * h }
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

	private fun adams(h: Double = this.h): List<Double> {

		val rung = runge(h)
		val x = List( ((b - a) / h).toInt() + 1 ) { index -> index * h }
		val y = rung.first.toMutableList()
		val z = rung.second.toMutableList()

		val koshiQI: (Int) -> Double = { i -> koshiQuestion(x[i], y[i], z[i]) }

		for (i in 4 .. x.lastIndex) {
			z[i] = z[i - 1] + h / 24 * (
					55 * koshiQI(i - 1) - 59 * koshiQI(i - 2) + 37 * koshiQI(i - 3) - 9 * koshiQI(i - 4)
					)
			y[i] = y[i - 1] + h * z[i - 1]
		}
		return y
	}

	private fun RRR(): Triple<List<Double>, List<Double>, List<Double>> {

		val x = List( ((b - a) / h).toInt() + 1 ) { index -> index * h }

		val eulerNorm = euler()
		val eulerHalf = euler(h / 2)

		val rungeNorm = runge().first
		val rungeHalf = runge(h / 2).first

		val adamsNorm = adams()
		val adamsHalf = adams(h / 2)

		val delta: (List<Double>, List<Double>, Int) -> Double = { listNorm, listHalf, i ->
			listNorm[i] + (listHalf[i * 2] - listNorm[i]) / 0.75
		}

		return Triple(
			List(x.size) { i -> delta(eulerNorm, eulerHalf, i) },
			List(x.size) { i -> delta(rungeNorm, rungeHalf, i) },
			List(x.size) { i -> delta(adamsNorm, adamsHalf, i) }
		)
	}

	operator fun invoke() {
		val x = List( ((b - a) / h).toInt() + 1 ) { index -> index * h }

		println("X: $x")
		println("Accurate y: ${x.map { accurateY(it) }}")
		println("Euler method: ${euler()}")
		println("Runge method: ${runge().first}")
		println("Adams method: ${adams()}")
		println()

		val rrr = RRR()
		println("Runge Rombert Richrdson method")
		println("Euler method: ${rrr.first}")
		println("Runge method: ${rrr.second}")
		println("Adams method: ${rrr.third}")
		println()

		val delta: (Int, Double) -> Double = { i, d -> abs(d - accurateY(x[i])) }
		println("Delta")
		println("Euler: ${rrr.first.mapIndexed(delta)}")
		println("Runge: ${rrr.second.mapIndexed(delta)}")
		println("Adams: ${rrr.third.mapIndexed(delta)}")
	}
}