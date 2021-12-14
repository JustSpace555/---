package sem2.lab6

import extensions.arrangeTo
import extensions.getSeries
import extensions.replace
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.stage.Stage
import sem2.extensions.solveTriDiagMethod
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Lab6 : Application() {

	private enum class Order { FIRST, SECOND }
	private enum class Approx { FIRST, SECOND }

	private fun exactSolution(x: List<Double>, t: List<Double>, a: Double): List<Double> {
		val aConst = a * t.last()
		return List(x.size) { i -> sin(x[i] - aConst) + cos(x[i] + aConst) }
	}

	private fun u(x: Double) = sin(x) + cos(x)
	private fun ut(x: Double, a: Double) = -a * u(x)



	private inner class FiniteDifferenceSolution(
		private val t: List<Double>,
		private val x: List<Double>,
		private val sigma: Double,
		private val tau: Double,
		private val a: Double,
		private val h: Double,
	) {
		fun explicit(
			grid: MutableList<MutableList<Double>>,
			approxFun: (Int) -> Unit
		) {
			for (i in 2..grid.lastIndex) {
				for (j in 1 until grid[i].lastIndex) {
					grid[i][j] = sigma * (grid[i - 1][j + 1] - 2 * grid[i - 1][j] + grid[i - 1][j - 1]) -
							grid[i - 2][j] + 2 * grid[i - 1][j]
				}
				approxFun(i)
			}
		}

		fun implicit(
			grid: MutableList<MutableList<Double>>,
			approxFun: (Int) -> Unit
		) {
			val vecD = grid[0].mapIndexed { i, d -> -d + 2 * grid[1][i] }.slice(1 until grid[0].lastIndex)
			val vecA = List(vecD.lastIndex) { -sigma }
			val vecB = List(vecD.size) { 1 + 2 * sigma }
			val vecC = List(vecD.lastIndex) { -sigma }

			for (i in 2..grid.lastIndex) {
				grid[i].replace {
					range = 1 until grid[i].lastIndex
					with = solveTriDiagMethod(vecA, vecB, vecC, vecD)
				}
				approxFun(i)
			}
		}

		fun solveFiniteDifferenceSolution(
			function: (
				grid: MutableList<MutableList<Double>>,
				approx: (Int) -> Unit,
			) -> Unit,
			order: Order,
			approx: Approx
		): List<Double> {
			val grid = MutableList(t.size) { MutableList(x.size) { 0.0 } }
			grid[0] = x.map(::u).toMutableList()

			fun onFirstApprox(i: Int) {
				grid[i][0] = grid[i][1] / (1 + h)
				grid[i][grid[i].lastIndex] = grid[i][grid[i].lastIndex - 1] / (1 - h)
			}

			fun onSecondApprox(i: Int) {
				grid[i][0] = (4 * grid[i][1] - grid[i][2]) / (2 * h + 3)
				grid[i][grid[i].lastIndex] =
					(4 * grid[i][grid[i].lastIndex - 1] - grid[i][grid[i].lastIndex - 2]) / (3 - 2 * h)
			}

			when (order) {
				Order.FIRST -> grid[1].replace {
					range = 1 until x.lastIndex
					with = x.mapIndexed { i, dot -> tau * ut(dot, a) + grid[0][i] }.slice(range)
				}
				Order.SECOND -> for (i in 1 until grid[1].lastIndex) {
					grid[1][i] =
						grid[0][i] + tau * ut(x[i], a) + sigma * (grid[0][i + 1] - 2 * grid[0][i] + grid[0][i - 1]) / 2
				}
			}

			val approxFun = when (approx) {
				Approx.FIRST -> ::onFirstApprox
				Approx.SECOND -> ::onSecondApprox
			}

			approxFun(1)

			function(grid, approxFun)

			return grid.last()
		}
	}

	override fun start(primaryStage: Stage?) {

		val t = 0.01
		val n = 25.0
		val k = 50.0
		val a = 1.0

		val tau = t / k
		val h = PI / n

		val sigma = (a * a * tau) / (h * h)

		val xDots = 0.0 arrangeTo PI + h withStep h
		val tDots = 0.0 arrangeTo t + tau withStep tau

		val analytical = exactSolution(xDots, tDots, a)
		println("Аналитическое решение: $analytical")

		val finiteDifferenceSolution = FiniteDifferenceSolution(tDots, xDots, sigma, tau, a, h)

		val explicitFirstOrderDots = finiteDifferenceSolution.solveFiniteDifferenceSolution(
			function = finiteDifferenceSolution::explicit,
			approx = Approx.SECOND,
			order = Order.FIRST
		)
		println("Явный метод конечных разностей первого порядка: $explicitFirstOrderDots")

		val implicitFirstOrderDots = finiteDifferenceSolution.solveFiniteDifferenceSolution(
			function = finiteDifferenceSolution::implicit,
			approx = Approx.SECOND,
			order = Order.FIRST
		)
		println("Неявный метод конечных разностей первого порядка: $implicitFirstOrderDots")

		val explicitSecondOrderDots = finiteDifferenceSolution.solveFiniteDifferenceSolution(
			function = finiteDifferenceSolution::explicit,
			approx = Approx.SECOND,
			order = Order.SECOND
		)
		println("Явный метод конечных разностей второго порядка: $explicitSecondOrderDots")

		val implicitSecondOrderDots = finiteDifferenceSolution.solveFiniteDifferenceSolution(
			function = finiteDifferenceSolution::implicit,
			approx = Approx.SECOND,
			order = Order.SECOND
		)
		println("Неявный метод конечных разностей второго порядка: $implicitSecondOrderDots")

		primaryStage?.let { stage ->
			val xSize = analytical.size
			val sizes = listOf(
				explicitFirstOrderDots, explicitSecondOrderDots, implicitFirstOrderDots, implicitSecondOrderDots
			).map { it.size }

			if (sizes.any { it != xSize }) throw IllegalStateException("Solutions have not equal sizes")

			val lineChart = LineChart(NumberAxis(), NumberAxis()).apply { title = "Лабораторная работа №6" }

			val x = List(xSize) { it }

			val analyticalSeries = "Аналитическое решение".getSeries(x, analytical)
			val explicitFirstOrderSeries = "Явная конечно-разностная схема первого порядка".getSeries(
				x, explicitFirstOrderDots
			)
			val implicitFirstOrderSeries = "Неявная конечно-разностная схема первого порядка".getSeries(
				x, implicitFirstOrderDots
			)
			val explicitSecondOrderSeries = "Явная конечно-разностная схема второго порядка".getSeries(
				x, explicitSecondOrderDots
			)
			val implicitSecondOrderSeries = "Неявная конечно-разностная схема второго порядка".getSeries(
				x, implicitSecondOrderDots
			)

			lineChart.data.addAll(
				analyticalSeries,
				explicitFirstOrderSeries,
				implicitFirstOrderSeries,
				explicitSecondOrderSeries,
				implicitSecondOrderSeries
			)

			stage.scene = Scene(lineChart, 1000.0, 1000.0)
			stage.show()
		}
	}

	operator fun invoke() = launch()
}

fun main() = Lab6()()