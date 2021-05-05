package lab3

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.stage.Stage
import lab1.Matrix
import lab1.SLAU
import kotlin.math.pow

class Lab3_3 : Application() {

	private val xi = listOf(-1.0, 0.0, 1.0, 2.0, 3.0, 4.0)
	private val yi = listOf(-1.8415, 0.0, 1.8415, 2.9093, 3.1411, 3.2432)

	override fun start(p0: Stage?) {

		val eps = 0.000000001

		val matrixFirstPolynomial = Matrix.of(listOf(
				listOf(xi.size, xi.sum(), yi.sum()),
				listOf(xi.sum(), xi.sumOf { it.pow(2) }, xi.zip(yi) { x, y -> x * y }.sum())
		))
		val aiFirst = SLAU(matrixFirstPolynomial).zeidelMethod(eps).first.transposed()
		val funFFirst: (Double) -> Double = { aiFirst[0][0] + aiFirst[0][1] * it }
		val funFFirstXList = xi.map(funFFirst)
		val sumSquareErrorFirst = funFFirstXList.zip(yi).sumOf { (it.first - it.second).pow(2) }

		val matrixPolynomialSecond = Matrix.of(listOf(
				listOf(xi.size, xi.sum(), xi.sumOf { it.pow(2) }, yi.sum()),

				listOf(
						xi.sum(),
						xi.sumOf { it.pow(2) },
						xi.sumOf { it.pow(3) },
						xi.zip(yi).sumOf { it.first * it.second }
				),
				listOf(
						xi.sumOf { it.pow(2) },
						xi.sumOf { it.pow(3) },
						xi.sumOf { it.pow(4) },
						xi.zip(yi).sumOf { it.first.pow(2) * it.second }
				)
		))
		val aiSecond = SLAU(matrixPolynomialSecond).zeidelMethod(eps).first.transposed()
		val funFSecond: (Double) -> Double = { aiSecond[0][0] + aiSecond[0][1] * it + aiSecond[0][2] * it * it }
		val funFSecondXList = xi.map(funFSecond)
		val sumSquareErrorSecond = funFSecondXList.zip(yi).sumOf { (it.first - it.second).pow(2) }

		println("Dots of first degree polynomial: ${funFFirstXList.joinToString(separator = ", ") { it.toString() }}")
		println("Sum of square errors of first degree polynomial: $sumSquareErrorFirst\n")
		println("Dots of second degree polynomial: ${funFSecondXList.joinToString(separator = ",") { it.toString() }}")
		println("Sum of square errors of second degree polynomial: $sumSquareErrorSecond")
		println("-------------------------------------------------------\n")

		p0?.let { stage ->
			stage.title = "Approaching first and second degree polynomials by Michael Moshkov"

			val xAxis = NumberAxis().apply { label = "x" }
			val yAxis = NumberAxis().apply { label = "y" }
			val lineChart = LineChart(xAxis, yAxis).apply { title = "Approaching first and second degree polynomials" }

			val ySeries = XYChart.Series<Number, Number>().apply {
				data.addAll(xi.zip(yi).map { XYChart.Data(it.first, it.second) })
				name = "Original polynomial"
			}
			Platform.runLater { ySeries.node.lookup(".chart-series-line").style = "-fx-stroke: black;" }

			val firstDegreeSeries = XYChart.Series<Number, Number>().apply {
				data.addAll(xi.zip(funFFirstXList).map { XYChart.Data(it.first, it.second) })
				name = "Approaching first degree polynomial"
			}
			val secondDegreeSeries = XYChart.Series<Number, Number>().apply {
				data.addAll(xi.zip(funFSecondXList).map { XYChart.Data(it.first, it.second) })
				name = "Approaching second degree polynomial"
			}

			lineChart.data.addAll(ySeries, firstDegreeSeries, secondDegreeSeries)
			val scene = Scene(lineChart, 800.0, 600.0)

			stage.scene = scene
			stage.show()
		}
	}

	operator fun invoke() {
		println("Exercise 3.3")
		launch()
	}
}