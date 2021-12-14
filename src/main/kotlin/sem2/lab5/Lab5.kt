package sem2.lab5

import extensions.arrangeTo
import extensions.getSeries
import extensions.read
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.stage.Stage
import sem2.extensions.solveTriDiagMethod
import java.lang.IllegalArgumentException
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class Lab5 : Application() {

	private fun function(x: Double) = x + sin(PI * x)

	private fun List<Double>.addBorders() = listOf(0.0) + this + 1.0

	private fun analyticalSolution(x: List<Double>, t: List<Double>, a: Double): List<Double> = List(x.size) { i ->
		x[i] + exp(-PI * PI * a * t[i]) * sin(PI * x[i])
	}

	private fun explicitFiniteDifferenceSolution(
		x: List<Double>,
		t: List<Double>,
		sigma: Double
	): List<Double> {
		var firstLayer = x.map { function(it) }
		val grid = mutableListOf(firstLayer)

		val secondLayer = MutableList(firstLayer.size) { 0.0 }
		secondLayer[secondLayer.lastIndex] = 1.0

		for (i in 1 until t.lastIndex) {
			for (j in 1 until x.lastIndex) {
				secondLayer[j] = (1 - 2 * sigma) * firstLayer[j] + sigma * (firstLayer[j - 1] + firstLayer[j + 1])
			}
			grid.add(secondLayer)
			firstLayer = secondLayer
		}

		return grid.last()
	}

	private fun implicitFiniteDifferenceSolution(x: List<Double>, t: List<Double>, sigma: Double): List<Double> {
		var vecD = x.map { function(it) }.slice(1 until x.lastIndex).toMutableList()

		val vecA = MutableList(vecD.size - 1) { -sigma }
		val vecC = MutableList(vecD.size - 1) { -sigma }
		val vecB = MutableList(vecD.size) { 1 + 2 * sigma }

		for (i in 1..t.lastIndex) {
			vecD = solveTriDiagMethod(vecA, vecB, vecC, vecD).toMutableList().apply { this[lastIndex] += sigma }
		}

		return vecD.apply { this[lastIndex] -= sigma }.addBorders()
	}

	private fun crankNickolsonSolution(x: List<Double>, t: List<Double>, oldSigma: Double): List<Double> {
		val sigma = oldSigma / 2
		var vecD = x.slice(1 until x.lastIndex).map { function(it) }

		val vecA = List(vecD.lastIndex) { -sigma }
		val vecB = List(vecD.size) { 2 + 2 * sigma }
		val vecC = List(vecD.lastIndex) { -sigma }

		val sigmaConst = 2 - 2 * sigma

		val temp = MutableList(vecD.size) { 0.0 }
		for (i in 1..t.lastIndex) {
			temp[0] = sigmaConst * vecD[0] + sigma * vecD[1]
			for (j in 1 until temp.lastIndex) {
				temp[j] = sigmaConst * vecD[j] + sigma * (vecD[j - 1] + vecD[j + 1])
			}
			temp[temp.lastIndex] = sigmaConst * vecD[temp.lastIndex - 1] + sigma * vecD[temp.lastIndex - 2]
			vecD = solveTriDiagMethod(vecA, vecB, vecC, temp)
			temp.replaceAll { 0.0 }
		}

		return vecD.addBorders()
	}

	override fun start(primaryStage: Stage?) {
		val t = read<Double>("Введите t") //0.01
		val n = read<Double>("Введите n") //25.0
		val k = read<Double>("Введите k") //50.0
		val a = read<Double>("Введите a") //1.0

		val tau = t / k
		val h = 1 / n
		val sigma = (a * a * tau) / (h * h)
		println("Sigma = $sigma\n")

		val xDots = 0.0 arrangeTo 1 + h withStep h
		val tDots = 0.0 arrangeTo t + tau withStep tau

		val analyticalSolutionDots = analyticalSolution(xDots, tDots, a)
		println("Аналитическое решение: $analyticalSolutionDots\n")

		val explicitFiniteDifferenceDots = explicitFiniteDifferenceSolution(xDots, tDots, sigma)
		println("Явный метод конечных разностей: $explicitFiniteDifferenceDots\n")

		val implicitFiniteDifferenceDots = implicitFiniteDifferenceSolution(xDots, tDots, sigma)
		println("Неявный метод конечных разностей: $implicitFiniteDifferenceDots\n")

		val crankNickolsonDots = crankNickolsonSolution(xDots, tDots, sigma)
		println("Метод Кранка-Николсона: $crankNickolsonDots\n")

		primaryStage?.let { stage ->
			val xSize = analyticalSolutionDots.size
			if (
				xSize != explicitFiniteDifferenceDots.size || xSize != implicitFiniteDifferenceDots.size ||
				xSize != crankNickolsonDots.size
			) {
				throw IllegalArgumentException("Solutions have not equal sizes")
			}

			val lineChart = LineChart(NumberAxis(), NumberAxis()).apply { title = "Лабораторная работа №5" }

			val x = List(xSize) { it }

			val analyticalSeries = "Аналитическое решение".getSeries(x, analyticalSolutionDots)
			val explicitSeries = "Явная конечно-разностная схема".getSeries(x, explicitFiniteDifferenceDots)
			val implicitSeries = "Неявная конечно-разностная схема".getSeries(x, implicitFiniteDifferenceDots)
			val crankSeries = "Схема Кранка-Николсона".getSeries(x, crankNickolsonDots)

			lineChart.data.addAll(analyticalSeries, explicitSeries, implicitSeries, crankSeries)

			stage.scene = Scene(lineChart, 1000.0, 1000.0)
			stage.show()
		}
	}

	operator fun invoke() = launch()
}

fun main() = Lab5()()