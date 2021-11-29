package lab5

import extensions.arrangeTo
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

private fun function(x: Double) = x + sin(PI * x)

private fun analyticalSolution(x: List<Double>, t: List<Double>, a: Double): List<Double> {
	val result = mutableListOf<Double>()
	for (i in x.indices) {
		result.add(
			x[i] + exp(-PI * PI * a * t[i]) * sin(PI * x[i])
		)
	}
	return result
}

private fun explicitFiniteDifferenceSolution(
	x: List<Double>,
	t: List<Double>,
	sigma: Double
): List<Double> {
	var firstLayer = List(x.size) { i -> function(x[i]) }
	val grid = MutableList(t.size) { MutableList(x.size) { 0.0 } }.apply {
		add(0, firstLayer.toMutableList())
	}

	val secondLayer = MutableList(firstLayer.size) { 0.0 }
	secondLayer[secondLayer.lastIndex] = 1.0

	for (i in 1 until t.lastIndex) {
		for (j in 1 until x.lastIndex) {
			secondLayer[j] = (1 - 2 * sigma) * firstLayer[j] + sigma * (firstLayer[j - 1] + firstLayer[j + 1])
		}
		grid.add(secondLayer)
		firstLayer = secondLayer
	}

	return grid.last()
}

private fun solveTriDiagMethod(
	vecA: List<Double>,
	vecB: List<Double>,
	vecC: List<Double>,
	vecD: List<Double>
): List<Double> {
	val p = MutableList(vecD.size) { 0.0 }.apply { this[0] = -vecC[0] / vecB[0] }
	val q = MutableList(vecD.size) { 0.0 }.apply { this[0] = vecD[0] / vecB[0] }

	for (i in 1 until vecD.lastIndex) {
		val calc = vecB[i] + vecA[i - 1] * p[i - 1]
		p[i] = -vecC[i] / calc
		q[i] = (vecD[i] - vecA[i - 1] * q[i - 1]) / calc
	}

	q[q.lastIndex] = (vecD.last() - vecA.last() * q[q.lastIndex - 1]) / (vecB.last() + vecA.last() * p[p.lastIndex - 1])

	val result = MutableList(vecD.size) { 0.0 }.apply { this[lastIndex] = q.last() }
	for (i in q.lastIndex - 1 downTo 0) result[i] = p[i] * result[i + 1] + q[i]

	return result
}

private fun implicitFiniteDifferenceSolution(x: List<Double>, t: List<Double>, sigma: Double): List<Double> {
	var vecD = MutableList(x.size) { i -> function(x[i]) }.slice(1 until x.lastIndex)
	val grid = mutableListOf(listOf(0.0) + vecD + 1.0)

	val vecA = MutableList(vecD.size - 1) { -sigma }
	val vecC = MutableList(vecD.size - 1) { -sigma }
	val vecB = MutableList(vecD.size) { 1 + 2 * sigma }

	for (i in 1..t.lastIndex) {
		vecD = solveTriDiagMethod(vecA, vecB, vecC, vecD).toMutableList()
		if (i < t.size - 1) vecD[vecD.lastIndex] += sigma
		grid.add(listOf(0.0) + vecD + 1.0)
	}

	return grid.last()
}

private fun crankNickolsonSolution(x: List<Double>, t: List<Double>, oldSigma: Double): List<Double> {
	val sigma = oldSigma / 2
	var vecD = x.map { function(it) }.slice(1 until x.lastIndex).toMutableList()

	val vecA = List(vecD.lastIndex) { -sigma }
	val vecB = List(vecD.size) { 2 + 2 * sigma }
	val vecC = List(vecD.lastIndex) { -sigma }

	val result = MutableList(t.size) { 0.0 }
	result[result.lastIndex] = 1.0

	val temp = MutableList(vecD.size) { 0.0 }
	for (i in 1 until t.lastIndex) {
		temp[0] = (2 - 2 * sigma) * vecD[0] + sigma * vecD[1]
		for (j in 1 until temp.lastIndex) {
			temp[j] = (2 - 2 * sigma) * vecD[j] + sigma * (vecD[j - 1] + vecD[j + 1])
		}
		temp[temp.lastIndex] = (2 - 2 * sigma) * vecD[i] + sigma * vecD[i - 1]
		vecD = solveTriDiagMethod(vecA, vecB, vecC, temp).toMutableList()
		temp.replaceAll { 0.0 }
	}

	return listOf(0.0) + vecD + listOf(1.0)
}

/**
 * Лабораторная работа №5. Вариант №2
 * @author Михаил Мошков, студент группы М8О-405Б-18
 */
fun main() {
	val t = /*read<Double>("Введите t")*/ 0.01
	val n = /*read<Double>("Введите n")*/ 25.0
	val k = /*read<Double>("Введите k")*/ 50.0
	val a = /*read<Double>("Введите a")*/ 1.0

	val tau = t / k
	val h = 1 / n
	val sigma = (a * a * tau) / (h * h)
	println("Sigma = $sigma\n")

	val xDots = 0.0 arrangeTo 1 + h withStep h
	val tDots = 0.0 arrangeTo t + tau withStep tau

	val analyticalSolutionDots = analyticalSolution(xDots, tDots, a)
	println("Аналитическое решение: $analyticalSolutionDots\n")

//	val explicitFiniteDifferenceDots = explicitFiniteDifferenceSolution(xDots, tDots, sigma)
//	println("Явный метод конечных разностей: $explicitFiniteDifferenceDots\n")

	val implicitFiniteDifferenceDots = implicitFiniteDifferenceSolution(xDots, tDots, sigma)
	println("Неявный метод конечных разностей: $implicitFiniteDifferenceDots\n")
//
//	val crankNickolsonDots = crankNickolsonSolution(xDots, tDots, sigma)
//	println("Метод Кранка-Николсона: $crankNickolsonDots\n")


}