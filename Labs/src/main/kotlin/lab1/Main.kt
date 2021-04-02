package lab1

/*
 *
 * Лабораторная работа №1
 * Выполнил студент: Мошков Михаил
 * Группа: М8О-305Б-18
 * Вариант 12
 *
*/

fun lab1_1(matrix: Matrix) {
	val lu = SLAU(matrix).luDecomposition()

	println("\nLower matrix:")
	println(lu.first.toPrint())

	println("\nUpper matrix:")
	println(lu.second.toPrint())

	println("\nDeterminant of lab1.SLAU: ${matrix.det}")

	println("\nReverse lab1.SLAU:")
	println(matrix.reverse().toPrint())

}

fun lab1_2(matrix: Matrix) {

	val answer = SLAU(matrix).runMethod()

	println("\nAll P: ${answer.first}")
	println("All Q: ${answer.second}")
	println("All x: ${answer.third}")
}

fun lab1_3(matrix: Matrix, epsilon: Double) {

	val iterationMethod = SLAU(matrix).easyIterationMethod(epsilon)

	println("\nAll x: ${iterationMethod.first.transposed()[0]}")
	println("Amount of iterations: ${iterationMethod.second}")
	println("Final epsilon: ${iterationMethod.third}")

	val zeidelMethod = SLAU(matrix).zeidelMethod(epsilon)
	println("\nAll x: ${zeidelMethod.first.transposed()[0]}")
	println("Amount of iterations: ${zeidelMethod.second}")
	println("Final epsilon: ${zeidelMethod.third}")
}

fun lab1_4(matrix: Matrix, epsilon: Double) {

	val answer = SLAU(matrix).rotationMethod(epsilon)
	println("\nOutput matrix:")
	println(answer.first.toPrint())
	println("\nAll x:")
	println(answer.second[0])
}

fun lab1_5(matrix: Matrix, epsilon: Double) {

	val answer = SLAU(matrix).exercise5(epsilon)
	println("\nNumber of iterations: ${answer.second}")
	println("\nOutput vector:")
	println(answer.first)
}

fun main() {

	println("***** 1.1 ******")

	val slau1_1 = Matrix.of(
		listOf(
			listOf(-1, -8, 0, 5),
			listOf(6, -6, 2, 4),
			listOf(9, -5, -6, 4),
			listOf(-5, 0, -9, 1)
		)
	)

	lab1_1(slau1_1)

	println("***** 1.1 ******\n\n\n")

	println("***** 1.2 ******")

	val slau1_2 = Matrix.of(
		listOf(
			listOf(0, -11, 9, -114),
			listOf(1, -8, 1, 81),
			listOf(-2, -11, 5, -8),
			listOf(-4, 12, 0, -40)
		)
	)

	lab1_2(slau1_2)

	println("\n***** 1.2 ******\n\n\n")



	println("***** 1.3 ******")

	val slau1_3 = Matrix.of(
		listOf(
			listOf(14, -4, -2, 3, 38),
			listOf(-3, 23, -6, -9, -195),
			listOf(-7, -8, 21, -5, -27),
			listOf(-2, -2, 8, 18, 142)
		)
	)

	lab1_3(slau1_3, 0.001)

	println("\n***** 1.3 *****\n\n\n")



	println("***** 1.4 *****")

	val slau1_4 = Matrix.of(
		listOf(
			listOf(7, 3, -1),
			listOf(3, -7, -8),
			listOf(-1, -8, -2)
		)
	)

	lab1_4(slau1_4, 0.1)

	println("\n***** 1.4 *****\n\n\n")



	println("***** 1.5 *****")

	val slau1_5 = Matrix.of(
		listOf(
			listOf(5, -1, -2),
			listOf(-4, 3, -3),
			listOf(-2, -1, 1)
		)
	)

	lab1_5(slau1_5, 0.01)

	println("\n****** 1.5 *****")
}
