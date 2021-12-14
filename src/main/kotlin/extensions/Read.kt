package extensions

import sem1.lab1.Matrix
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException
import java.util.*

inline fun <reified T : Any> read(message: String = ""): T {
	println(message)

	return when (T::class) {
		Double::class -> readln().toDouble() as T
		Int::class -> readln().toInt() as T
		else -> throw IllegalArgumentException("Указан неверный тип для считывания со стандартного ввода")
	}
}

fun readMatrix(n: Int, m: Int, message: String = ""): Matrix {
	println(message)

	val input = mutableListOf<List<Double>>()
	for (i in 0 until n) {
		val inputRow = readln().split(' ')
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