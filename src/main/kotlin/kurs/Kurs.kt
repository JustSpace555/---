package kurs

import kotlin.math.pow

/*
	Курсова работа
	Выполнил студент группы М8О-305Б-18
	Мошков Михаил
	Тема №12: Численное решение жестких систем ОДУ с использованием неявных методов Рунге-Кутты
*/

private const val a = 0.0                                                           // Начальная граница
private const val b = 1.0                                                           // Конечная граница
private const val h = 0.1                                                           // Шаг
private const val y0 = 1.0                                                          // y(0) = ...
private const val yStroke0 = 1.0                                                    // y'(0) = ...
private val yFun: (Double) -> Double = { x -> x - x.pow(2) + 1 }                 // Функция y(x)
private val yStrokeFun: (Double, Double) -> Double = {x, y -> }                     // Функция y'(x, y)
private val koshiQuestion: (Double, Double, Double) -> Double = { x, y, z ->        // y'' = ...
	(2 * x * z - 2 * y) / (x.pow(2) + 1)
}

private fun rungeSecond() {
	val x = List( ((b - a) / h).toInt() + 1 ) { index -> a + index * h }
	val y = MutableList(x.size) { y0 }
	val z = MutableList(x.size) { yStroke0 }

	for (i in 1.. x.lastIndex) {

	}
}

fun main() {

}