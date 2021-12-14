package sem1.lab2

/*
 *
 * Лабораторная работа №1
 * Выполнил студент: Мошков Михаил
 * Группа: М8О-305Б-18
 * Вариант 12
 *
*/

fun main() {

	println("***** 2.1 *****")

	println("\n3^x - 5x^2 + 1 = 0")
	println("Iterations method")
	val i1Answer = IterationMethod.exercise1(0.5, 0.001, 0.1)
	println("Answer: ${i1Answer.first}")
	println("Iterations: ${i1Answer.second}")

	println("\nNewtonMethod")
	val n1Answer = NewtonMethod.exercise1(1.0, 0.001)
	println("Answer: ${n1Answer.first}")
	println("Iterations: ${n1Answer.second}")

	println("\n***** 2.1 *****\n\n\n")



	println("***** 2.2 *****")

	println("\nx1 - cos(x2) = 3")
	println("x2 - sin(x1) = 3")
	println("Iterations method")
	val i2Answer = IterationMethod.exercise2(Pair(0.0, 0.0), 0.001, 0.2)
	println("Answer: x1 = ${i2Answer.first}, x2 = ${i2Answer.second}")
	println("Iterations: ${i2Answer.third}")

	println("\nNewton method")
	val n2Answer = NewtonMethod.exercise2(Pair(2.0, 4.0), 0.001)
	println("Answer: x1 = ${n2Answer.first}, x2: ${n2Answer.second}")
	println("Iterations: ${n2Answer.third}")

	println("\n***** 2.2 *****")
}