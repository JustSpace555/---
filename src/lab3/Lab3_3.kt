package lab3

import lab1.Matrix
import lab1.SLAU

object Lab3_3 {

	private val xi = listOf(
//		-1.0, 0.0, 1.0, 2.0, 3.0, 4.0
		0.0, 1.7, 3.4, 5.1,	6.8, 8.5
	)
	private val yi = listOf(
//		-1.8415, 0.0, 1.8415, 2.9093, 3.1411, 3.2432
		0.0, 1.3038, 1.8439, 2.2583, 2.6077, 2.9155
	)


	operator fun invoke() {
		val matrix = Matrix.of(listOf(
			listOf(6.0, xi.sum(), yi.sum()),
			listOf(xi.sum(), xi.sumOf { it * it }, xi.zip(yi) { x, y -> x * y }.sum())
		))
		val a = SLAU(matrix).zeidelMethod(0.000000000000001)
		println(a.first.transposed().toPrint())
		println(a.second)
		println(a.third)
	}
}