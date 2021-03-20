import java.lang.StringBuilder

/*
 *
 * Лабораторная работа №1
 * Выполнил студент: Мошков Михаил
 * Группа: М8О-305Б-18
 *
*/

fun lab1_1(matrix: Matrix) {
	val lu = SLAU(matrix).luDecomposition()

	println("\nLower matrix:")
	println(lu.first.toPrint())

	println("\nUpper matrix:")
	println(lu.second.toPrint())

	println("\nDeterminant of SLAU: ${matrix.det}")

	println("\nReverse SLAU:")
	println(matrix.reverse().toPrint(false))

}

fun lab1_2(matrix: Matrix) {

	val answer = SLAU(matrix).runMethod()

	println("\nAll P: ${answer.first.listToString()}")
	println("All Q: ${answer.second.listToString()}")
	println("All x: ${answer.third.listToString()}")
}

fun lab1_3(matrix: Matrix, epsilon: Double) {

	val answer = SLAU(matrix).easyIterationMethod(epsilon)

	println("\nAll x: ${answer.first.transposed()[0].listToString()}")
	println("Amount of iterations: ${answer.second}")
	println("Final epsilon: ${answer.third}")
}

val listToString: List<Double>.() -> String = {
	val sb = StringBuilder()
	this.forEachIndexed { i, el -> sb.append("$el$i ") }
	sb.toString()
}

fun main() {

	println("***** 1.1 ******")

	val slau1_1 = arrayOf(
		arrayOf(-6, -5, -3, -8),
		arrayOf(5, -1, -5, -4),
		arrayOf(-6, 0, 5, 5),
		arrayOf(-7, -2, 8, 5)
	).map { it.toList() }

	lab1_1(Matrix(slau1_1))

	println("***** 1.1 ******\n\n\n")



	println("***** 1.2 ******")

	val slau1_2 = arrayOf(
		arrayOf(0, 14, 9, 125),
		arrayOf(-8, 14, 6, -56),
		arrayOf(-5, -17, 8, 144),
		arrayOf(-4, -10, 0, 70)
	)

	lab1_2(Matrix(slau1_2))

	println("\n***** 1.2 ******\n\n\n")



	println("***** 1.3 ******")

	val slau1_3 = arrayOf(
		arrayOf(24, -7, -4, 4, -190),
		arrayOf(-3, -9, -2, -2, -12),
		arrayOf(3, 7, 24, 9, 155),
		arrayOf(1, -6, -2, -15, -17)
	)

	lab1_3(Matrix(slau1_3), 0.001)
}
