package sem2.lab8

import extensions.arrangeTo
import sem1.lab1.Matrix
import sem2.extensions.solveTriDiagMethod
import java.io.File
import java.util.*
import kotlin.math.*

class Lab8 {

	private fun getCalc(t: Double, mu1: Double, mu2: Double, a: Double) =
		exp(-(mu1.pow(2) + mu2.pow(2)) * a * t)

	private fun ux(y: Double, t: Double, mu1: Double, mu2: Double, a: Double) =
		cos(mu2 * y) * getCalc(t, mu1, mu2, a)
	private fun ux(y: List<Double>, t: Double, mu1: Double, mu2: Double, a: Double) = y.map { yi ->
		cos(mu2 * yi) * getCalc(t, mu1, mu2, a)
	}

	private fun uy(x: Double, t: Double, mu1: Double, mu2: Double, a: Double) =
		cos(mu1 * x) * getCalc(t, mu1, mu2, a)
	private fun uy(x: List<Double>, t: Double, mu1: Double, mu2: Double, a: Double) = x.map { xi ->
		cos(mu1 * xi) * getCalc(t, mu1, mu2, a)
	}

	private fun ut(x: Double, y: Double, mu1: Double, mu2: Double) = cos(mu1 * x) * cos(mu2 * y)
	private fun ut(x: List<Double>, y: List<Double>, mu1: Double, mu2: Double) = x.zip(y).map { (xi, yi) ->
		cos(mu1 * xi) * cos(mu2 * yi)
	}

	fun alternatingDir(
		x: List<Double>,
		y: List<Double>,
		t: List<Double>,
		mu1: Double,
		mu2: Double,
		a: Double
	): List<Matrix> {
		val grid = MutableList(t.size) { Matrix.getEmptyMatrix(x.size, y.size) }
		val dx = x[1] - x[0]
		val dy = y[1] - y[0]
		val dt = t[1] - t[0]

		val sigma1 = a * dt / dx.pow(2)
		val sigma2 = a * dt / dy.pow(2)

		for (j in y.indices)
			for (i in x.indices)
				grid[0][i, j] = ut(x[i], y[j], mu1, mu2)

		for (k in 1..t.lastIndex) {
			for (j in y.indices) grid[k][0, j] = ux(y[j], t[k], mu1, mu2, a)
			for (i in x.indices) grid[k][i, 0] = uy(x[i], t[k], mu1, mu2, a)
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

				vecD1[0] = uy(x[i], t[k] - dt / 2, mu1, mu2, a)
				vecD1[vecD1.lastIndex] = 0.0
				tempGrid.replaceRow(i, solveTriDiagMethod(vecA1, vecB1, vecC1, vecD1))
			}

			with(tempGrid) {
				replaceRow(0, ux(y, t[k] - dt / 2, mu1, mu2, a))
				replaceColumn(0, uy(x, t[k] - dt / 2, mu1, mu2, a))
				replaceRow(rowsLastIndex, List(columns) { 0.0 })
				replaceColumn(columnLastIndex, List(rows) { 0.0 })
			}

			for (j in 1 until y.lastIndex) {
				for (i in 1 until x.lastIndex)
					vecD2[i] = (-sigma1 / 2) * (
						tempGrid[i, j + 1] - 2 * tempGrid[i, j] + tempGrid[i, j - 1]
					) - tempGrid[i, j]

				vecD2[0] = ux(y[j], t[k], mu1, mu2, a)
				vecD2[vecD2.lastIndex] = 0.0
				grid[k].replaceColumn(j, solveTriDiagMethod(vecA2, vecB2, vecC2, vecD2))
			}

			with(grid[k]) {
				replaceRow(0, ux(y, t[k], mu1, mu2, a))
				replaceColumn(0, uy(x, t[k], mu1, mu2, a))
				replaceColumn(columnLastIndex, List(rows) { 0.0 })
				replaceRow(rowsLastIndex, List(columns) { 0.0 })
			}
		}

		grid.replaceAll { it.transposed() }
		return grid
	}

	fun fractionSteps(
		x: List<Double>,
		y: List<Double>,
		t: List<Double>,
		mu1: Double,
		mu2: Double,
		a: Double
	): List<Matrix> {
		val grid = MutableList(t.size) { Matrix.getEmptyMatrix(x.size, y.size) }
		val dx = x[1] - x[0]
		val dy = y[1] - y[0]
		val dt = t[1] - t[0]

		val sigma1 = a * dt / dx.pow(2)
		val sigma2 = a * dt / dy.pow(2)

		for (j in y.indices)
			for (i in x.indices)
				grid[0][i, j] = ut(x[i], y[i], mu1, mu2)

		for (k in 1..t.lastIndex) {
			for (j in y.indices) grid[k][0, j] = ux(y[j], t[k], mu1, mu2, a)
			for (i in x.indices) grid[k][i, 0] = uy(x[i], t[k], mu1, mu2, a)
		}

		val vecA1 = MutableList(y.size) { sigma1 }.apply { this[lastIndex] = 0.0 }
		val vecB1 = MutableList(y.size) { -1 -2 * sigma1 }.apply { this[0] = 1.0; this[lastIndex] = 1.0 }
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
				vecD1[0] = uy(x[i], t[k] - dt / 2, mu1, mu2, a)
				vecD1[vecD1.lastIndex] = 0.0
				tempGrid.replaceRow(i, solveTriDiagMethod(vecA1, vecB1, vecC1, vecD1))
			}

			with(tempGrid) {
				replaceRow(0, ux(y, t[k] - dt / 2, mu1, mu2, a))
				replaceColumn(0, uy(x, t[k] - dt / 2, mu1, mu2, a))
				replaceColumn(columnLastIndex, List(rows) { 0.0 })
				replaceRow(rowsLastIndex, List(columns) { 0.0 })
			}

			for (j in 1 until y.lastIndex) {
				for (i in 1 until x.lastIndex) vecD2[i] = -tempGrid[i, j]
				vecD2[0] = ux(y[j], t[k], mu1, mu2, a)
				vecD2[vecD2.lastIndex] = 0.0
				grid[k].replaceColumn(j, solveTriDiagMethod(vecA2, vecB2, vecC2, vecD2))
			}

			with(grid[k]) {
				replaceRow(0, ux(y, t[k], mu1, mu2, a))
				replaceColumn(0, uy(x, t[k], mu1, mu2, a))
				replaceColumn(columnLastIndex, List(rows) { 0.0 })
				replaceRow(rowsLastIndex, List(columns) { 0.0 })
			}
		}

		grid.replaceAll { it.transposed() }
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

	val lab = Lab8()
	val alternating = lab.alternatingDir(x, y, t, mu1, mu2, a)
	val fractionSteps = lab.fractionSteps(x, y, t, mu1, mu2, a)

	val labPath = "src/main/kotlin/sem2/lab8"

	File(labPath / "x").writeText(x.joinToString(" "))
	File(labPath / "y").writeText(y.joinToString(" "))

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

	val scanner = Scanner(process.errorStream)
	while (scanner.hasNextLine()) println(scanner.nextLine())
}