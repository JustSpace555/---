package extensions

import javafx.scene.chart.XYChart

fun String.getSeries(x: List<Int>, y: List<Double>) = XYChart.Series<Number, Number>().apply {
	data.addAll(x.zip(y).map { XYChart.Data(it.first, it.second) })
	name = this@getSeries
}