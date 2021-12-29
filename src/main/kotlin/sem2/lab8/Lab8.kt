package sem2.lab8

import extensions.arrangeTo
import sem1.lab1.Matrix
import sem2.extensions.solveTriDiagMethod
import java.io.File
import java.util.*
import kotlin.math.*

class Lab8(
	private val x: List<Double>,
	private val y: List<Double>,
	private val t: List<Double>,
	private val mu1: Double,
	private val mu2: Double,
	private val a: Double
) {

	private fun getCalc(t: Double) = exp(-(mu1.pow(2) + mu2.pow(2)) * a * t)

	private fun ux(y: Double, t: Double) = cos(mu2 * y) * getCalc(t)
	private fun ux(t: Double) = y.map { yi -> cos(mu2 * yi) * getCalc(t) }

	private fun uy(x: Double, t: Double) = cos(mu1 * x) * getCalc(t)
	private fun uy(t: Double) = x.map { xi -> cos(mu1 * xi) * getCalc(t) }

	private fun ut(x: Double, y: Double) = cos(mu1 * x) * cos(mu2 * y)

	private fun Matrix.changeGrid(row: List<Double>, column: List<Double>) {
		replaceRow(0, row)
		replaceColumn(0, column)
		replaceRow(rowsLastIndex, List(columns) { 0.0 })
		replaceColumn(columnLastIndex, List(rows) { 0.0 })
	}

	fun alternatingDir(): List<Matrix> {
		val grid = MutableList(t.size) { Matrix.getEmptyMatrix(x.size, y.size) }
		val dt = t[1] - t[0]

		val sigma1 = a * dt / (x[1] - x[0]).pow(2)
		val sigma2 = a * dt / (y[1] - y[0]).pow(2)

		y.forEachIndexed { j, yj ->
			x.forEachIndexed { i, xi ->
				grid[0][i, j] = ut(xi, yj)
			}
		}

		for (k in 1..t.lastIndex) {
			y.forEachIndexed { j, yj -> grid[k][0, j] = ux(yj, t[k]) }
			x.forEachIndexed { i, xi -> grid[k][i, 0] = uy(xi, t[k]) }
		}

		val vecA1 = MutableList(y.size) { sigma1 / 2 }.apply { this[lastIndex] = 0.0 }
		val vecB1 = MutableList(y.size) { -1 -sigma1 }.apply { this[0] = 1.0; this[lastIndex] = 1.0 }
		val vecC1 = MutableList(y.size) { sigma1 / 2 }.apply { this[0] = 0.0 }
		val vecD1 = MutableList(y.size) { 0.0 }

		val vecA2 = MutableList(x.size) { sigma2 / 2 }.apply { this[lastIndex] = 0.0 }
		val vecB2 = MutableList(x.size) { -1 - sigma2 }.apply { this[0] = 1.0; this[lastIndex] = 1.0 }
		val vecC2 = MutableList(x.size) { sigma2 / 2 }.apply { this[0] = 0.0 }
		val vecD2 = MutableList(x.size) { 0.0 }

		for (k in 1..t.lastIndex) {
			val tempGrid = Matrix.getEmptyMatrix(x.size, y.size)

			for (i in 1 until x.lastIndex) {
				for (j in 1 until y.lastIndex) {
					vecD1[j] = (-sigma2 / 2) * (
							grid[k - 1][i, j + 1] - 2 * grid[k - 1][i, j] + grid[k - 1][i, j - 1]
							) - grid[k - 1][i, j]
				}

				vecD1.apply {
					set(0, uy(x[i], t[k] - dt / 2))
					set(lastIndex, 0.0)
				}
				tempGrid.replaceRow(i, solveTriDiagMethod(vecA1, vecB1, vecC1, vecD1))
			}

			tempGrid.changeGrid(ux(t[k] - dt / 2), uy(t[k] - dt / 2))

			for (j in 1 until y.lastIndex) {
				for (i in 1 until x.lastIndex)
					vecD2[i] = (-sigma1 / 2) * (
						tempGrid[i, j + 1] - 2 * tempGrid[i, j] + tempGrid[i, j - 1]
					) - tempGrid[i, j]

				vecD2.apply {
					set(0, ux(y[j], t[k]))
					set(lastIndex, 0.0)
				}
				grid[k].replaceColumn(j, solveTriDiagMethod(vecA2, vecB2, vecC2, vecD2))
			}

			grid[k].changeGrid(ux(t[k]), uy(t[k]))
		}

		return grid
	}

	fun fractionSteps(): List<Matrix> {
		val grid = MutableList(t.size) { Matrix.getEmptyMatrix(x.size, y.size) }
		val dt = t[1] - t[0]

		val sigma1 = a * dt / (x[1] - x[0]).pow(2)
		val sigma2 = a * dt / (y[1] - y[0]).pow(2)

		y.forEachIndexed { j, yj ->
			x.forEachIndexed { i, xi ->
				grid[0][i, j] = ut(xi, yj)
			}
		}

		for (k in 1..t.lastIndex) {
			y.forEachIndexed { j, yj -> grid[k][0, j] = ux(yj, t[k]) }
			x.forEachIndexed { i, xi -> grid[k][i, 0] = uy(xi, t[k]) }
		}

		val vecA1 = MutableList(y.size) { sigma1 }.apply { this[lastIndex] = 0.0 }
		val vecB1 = MutableList(y.size) { -1 - 2 * sigma1 }.apply { this[0] = 1.0; this[lastIndex] = 1.0 }
		val vecC1 = MutableList(y.size) { sigma1 }.apply { this[0] = 0.0 }
		val vecD1 = MutableList(y.size) { 0.0 }

		val vecA2 = MutableList(x.size) { sigma2 }.apply { this[lastIndex] = 0.0 }
		val vecB2 = MutableList(x.size) { -1 - 2 * sigma2 }.apply { this[0] = 1.0; this[lastIndex] = 1.0 }
		val vecC2 = MutableList(x.size) { sigma2 }.apply { this[0] = 0.0 }
		val vecD2 = MutableList(x.size) { 0.0 }

		for (k in 1 .. t.lastIndex) {
			val tempGrid = Matrix.getEmptyMatrix(x.size, y.size)

			for (i in 1 until x.lastIndex) {
				for (j in 1 until y.lastIndex) vecD1[j] = -grid[k - 1][i, j]

				vecD1.apply {
					set(0, uy(x[i], t[k] - dt / 2))
					set(lastIndex, 0.0)
				}
				tempGrid.replaceRow(i, solveTriDiagMethod(vecA1, vecB1, vecC1, vecD1))
			}

			tempGrid.changeGrid(ux(t[k] - dt / 2), uy(t[k] - dt / 2))

			for (j in 1 until y.lastIndex) {
				for (i in 1 until x.lastIndex) vecD2[i] = -tempGrid[i, j]

				vecD2.apply {
					set(0, ux(y[j], t[k]))
					set(lastIndex, 0.0)
				}
				grid[k].replaceColumn(j, solveTriDiagMethod(vecA2, vecB2, vecC2, vecD2))
			}

			grid[k].changeGrid(ux(t[k]), uy(t[k]))
		}

		return grid
	}
}

private operator fun String.div(other: String) = "$this/$other"

fun main() {
	val a = 1.0
	val mu1 = 1.0
	val mu2 = 2.0

	val dx = PI / 32
	val dy = PI / 32
	val dt = 0.01

	val ly = mu2 * PI / 2
	val lx = mu1 * PI / 2
	val lt = 0.1

	val x = 0.0 arrangeTo lx + dx withStep dx
	val y = 0.0 arrangeTo ly + dy withStep dy
	val t = 0.0 arrangeTo lt + dt withStep dt

	val lab = Lab8(x, y, t, mu1, mu2, a)
	val alternating = lab.alternatingDir()
	val fractionSteps = lab.fractionSteps()

	val labPath = "src/main/kotlin/sem2/lab8"

	File(labPath / "x").writeText(x.joinToString(" "))
	File(labPath / "y").writeText(y.joinToString(" "))
	File(labPath / "t").writeText(t.joinToString(" "))

	File(labPath / "alternating").writeText(
		alternating.joinToString("\n") { matrix ->
			matrix.array.joinToString("\n") { row -> row.joinToString(" ") }
		}
	)

	File(labPath / "fractionSteps").writeText(
		fractionSteps.joinToString("\n") { matrix ->
			matrix.array.joinToString("\n") { row -> row.joinToString(" ") }
		}
	)

	//Нет поддержки для запуска в Windows
	val process = Runtime.getRuntime().exec("python3 ${labPath / "lab8.py"}")

	val errorScanner = Scanner(process.errorStream)
	val outputScanner = Scanner(process.inputStream)
	while (errorScanner.hasNextLine()) println(errorScanner.nextLine())
	while(outputScanner.hasNextLine()) println(outputScanner.nextLine())
}