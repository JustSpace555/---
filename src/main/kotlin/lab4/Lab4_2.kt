package lab4

import lab1.Matrix
import lab1.SLAU
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import kotlin.math.E
import kotlin.math.abs
import kotlin.math.pow

object Lab4_2 {

	private const val a = 1
	private const val b = 2
//	3
	private const val h = 0.1
	private const val eps = 0.001

	private val accurateY: (Double) -> Double = { x ->
//		2 + x + 2 * x * ln(abs(x))
		E.pow(x) * x.pow(2)
	}
	private val koshiQ: (Double, Double, Double) -> Double = { x, y, z ->
//		(x * z - y) / (x * (x - 1))
		((2 * x + 1) * z - (x + 1) * y) / x
	}

	private fun shootingMethod(h: Double = this.h): List<Double> {
		val y0 = E
//			accurateY(1.0)
		val y1 = 4 * E.pow(2)
//			accurateY(3.0)
		var nu1 = 1.0
		var nu2 = 0.8

		var f1 = Lab4_1(
			a = a, b = b, h = h, y0 = y0,
			yStroke0 = nu1,
			koshiQuestion = koshiQ
		).rungeY().last() - y1

		var f2 = Lab4_1(
			a = a, b = b, h = h, y0 = y0,
			yStroke0 = nu2,
			koshiQuestion = koshiQ
		).rungeY().last() - y1

		var temp: Double
		while (abs(f1) > eps) {
			temp = nu2
			nu2 -= f2 * (nu2 - nu1) / (f2 - f1)
			nu1 = temp
			f1 = f2
			f2 = Lab4_1(
				a = a, b = b, h = h, y0 = y0,
				yStroke0 = nu2, koshiQuestion = koshiQ
			).rungeY().last() - y1
		}

		return Lab4_1(a = a, b = b, h = h, y0 = y0, yStroke0 = nu2, koshiQuestion = koshiQ).rungeY().dropLast(1)
	}

	private fun finalDifferenceMethod(h: Double = this.h): List<Double> {
		val alpha = 0.0
		val beta = 1.0
		val delta = 1.0
		val gamma = -2.0
//			-3.0
		val y0 = 3 * E
//			3.0
		val y1 = 0.0
//			-4.0

		val x = List( ((b - a) / h).toInt() ) { i -> a + h * i }
		val yCoefficient: (Double) -> Double = { d ->
//			1 / (d * (d - 1))
			(d + 1) / d
		}
		val yStrokeCoefficient: (Double) -> Double = { d ->
//			-1 / (d - 1)
			(-2 * d + 1) / d
		}

		//Cнизу
		val aRun = mutableListOf(0.0)
		//Главная
		val bRun = mutableListOf(alpha * h - beta)
		//Сверху
		val cRun = mutableListOf(beta)
		//Ответы
		val dRun = mutableListOf(y0 * h)
		for (i in 1 until x.lastIndex) {
			aRun += 1 + yStrokeCoefficient(x[i]) * h / 2
			bRun += yCoefficient(x[i]) * h.pow(2) - 2
			cRun += 1 + yStrokeCoefficient(x[i]) * h / 2
			dRun += 0.0
		}
		aRun += -gamma
		bRun += delta * h + gamma
		cRun += 0.0
		dRun += y1 * h

		val matrix = mutableListOf<MutableList<Double>>()
		for (i in x.indices) {
			val row = mutableListOf<Double>()
			for (j in x.indices)
				row += when(i) {
					j -> bRun[j]
					j + 1 -> aRun[j]
					j - 1 -> cRun[j]
					else -> 0.0
				}
			matrix.add(row)
		}

		println(Matrix.of(
			matrix
				.flatten()
				.map { BigDecimal(it).setScale(3, RoundingMode.HALF_EVEN).toDouble() }
				.chunked(x.size)
		).toPrint())
		return SLAU(
			Matrix.of(matrix).concat(Matrix.of(listOf(dRun)).transposed())
		).runMethod().third
	}

	operator fun invoke() {
		val x = List( ((b - a) / h).toInt() ) { i -> a + i * h }
		println("X: $x")
		println("Accurate y: ${x.map { accurateY(it) }}")
		println("Shooting method: ${shootingMethod()}")

		val shootingR = Lab4_1.rrr(h, ::shootingMethod)
		val delta: (Int, Double) -> Double = { i, d -> abs(d - accurateY(x[i])) }
		println("Runge Rombert Richrdson method:")
		println("Shooting: $shootingR")
		println("Delta")
		println("Shooting: ${shootingR.mapIndexed(delta)}")
		println("------------------------------------------")
		println("Final Difference method: ${finalDifferenceMethod()}")
	}
}