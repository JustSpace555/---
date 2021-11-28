package extensions

import lab1.Matrix
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException
import java.util.*

val scanner = Scanner(System.`in`)

inline fun <reified T : Any> read(message: String = ""): T {
	println(message)

	return when (T::class) {
		Double::class -> scanner.nextLine().toDouble() as T
		Int::class -> scanner.nextLine().toInt() as T
		else -> throw IllegalArgumentException("Указан неверный тип для считывания со стандартного ввода")
	}
}

fun readMatrix(n: Int, m: Int, message: String = ""): Matrix {
	println(message)

	val input = mutableListOf<List<Double>>()
	for (i in 0 until n) {
		val inputRow = scanner.nextLine().split(' ')
		if (inputRow.size != m)
			throw IllegalArgumentException("Размер введенной строки больше необходимого")
		try {
			input.add(inputRow.map { it.toDouble() })
		} catch (e: NumberFormatException) {
			throw NumberFormatException("Невверный формат входного числа для матрицы")
		}
	}
	return Matrix.of(input)
}