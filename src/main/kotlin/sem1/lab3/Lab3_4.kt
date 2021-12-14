package sem1.lab3

import java.lang.IllegalStateException

object Lab3_4 {

	private const val xStar = 0.2
	private val xi = listOf(-1.0, -0.4, 0.2, 0.6, 1.0)
	private val yi = listOf(-1.4142, -0.55838, 0.27870, 0.84008, 1.4142)

	private val i = xi.indexOf(
		xi.find { xStar <= it } ?: throw IllegalStateException("Wrong table or X*")
	)

	operator fun invoke() {
		val leftBorder = (yi[i] - yi[i - 1]) / (xi[i] - xi[i - 1])
		val rightBorder = (yi[i + 1] - yi[i]) / (xi[i + 1] - xi[i])

		val answer = rightBorder + (leftBorder - rightBorder) / (xi[i + 1] - xi[i - 1]) * (2 * xStar - xi[i - 1] - xi[i])
		println("Exercise 3.4: $answer")
		println("-------------------------------------------------------\n")
	}
}