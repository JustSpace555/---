package sem2.lab7

import extensions.arrangeTo
import sem1.lab1.Matrix
import java.io.File
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

class Lab7 {

	private fun uy(y: List<Double>) = y.map { 1 - it.pow(2) }

	private fun ux(x: List<Double>) = x.map { it.pow(2) - 1 }

	private fun Matrix.calcPrevMatrixDiff(i: Int, j: Int) =
		(this[i + 1, j] + this[i - 1, j] + this[i, j - 1] + this[i, j + 1]) / 4

	private fun calcNorm(currentMatrix: Matrix, prevMatrix: Matrix): Double {
		var max = 0.0
		for (i in currentMatrix.rowIndices)
			for (j in currentMatrix.columnIndices) {
				val calc = abs(currentMatrix[i][j] - prevMatrix[i][j])
				if (calc > max) max = calc
			}

		return max
	}

	private fun getPreCalculationMatrices(x: List<Double>, y: List<Double>): Pair<Matrix, Matrix> {
		val currentMatrix = Matrix.getEmptyMatrix(y.size, x.size).apply {
			replaceRow(y.lastIndex, ux(x))
			replaceColumn(x.lastIndex, uy(y))
		}

		val prevMatrix = Matrix.getEmptyMatrix(y.size, x.size)

		return currentMatrix to prevMatrix
	}

	private fun liebmanMethod(x: List<Double>, y: List<Double>, epsilon: Double): Matrix {
		var (currentMatrix, prevMatrix) = getPreCalculationMatrices(x, y)

		while (calcNorm(currentMatrix, prevMatrix) > epsilon) {
			prevMatrix = Matrix.of(currentMatrix.array)
			currentMatrix.apply {
				replaceRow(0, this[1])
				replaceColumn(0, getColumn(1))
			}

			for (i in 1 until y.lastIndex)
				for (j in 1 until x.lastIndex)
					currentMatrix[i, j] = prevMatrix.calcPrevMatrixDiff(i, j)
		}

		return currentMatrix
	}

	private fun relaxationMethod(x: List<Double>, y: List<Double>, epsilon: Double, c: Double): Matrix {
		var (currentMatrix, prevMatrix) = getPreCalculationMatrices(x, y)

		while (calcNorm(currentMatrix, prevMatrix) > epsilon) {
			prevMatrix = Matrix.of(currentMatrix.array)
			currentMatrix.apply {
				replaceRow(0, this[1])
				replaceColumn(0, getColumn(1))
			}

			for (i in 1 until y.lastIndex)
				for (j in 1 until x.lastIndex)
					currentMatrix[i, j] = (1 - c) * prevMatrix[i, j] + c * prevMatrix.calcPrevMatrixDiff(i, j)
		}

		return currentMatrix
	}

	private fun zeidelMethod(x: List<Double>, y: List<Double>, epsilon: Double, c: Double): Matrix {
		var (currentMatrix, prevMatrix) = getPreCalculationMatrices(x, y)

		while (calcNorm(currentMatrix, prevMatrix) > epsilon) {
			prevMatrix = Matrix.of(currentMatrix.array)
			currentMatrix.apply {
				replaceRow(0, this[1])
				replaceColumn(0, getColumn(1))

				for (i in 1 until y.lastIndex)
					for (j in 1 until x.lastIndex)
						currentMatrix[i, j] = (1 - c) * prevMatrix[i, j] + c * (
								prevMatrix[i + 1, j] + currentMatrix[i - 1][j]
										+ currentMatrix[i, j - 1] + prevMatrix[i, j + 1]
								) / 4
			}
		}

		return currentMatrix
	}

	private operator fun String.div(other: String) = "$this/$other"

	operator fun invoke() {
		val dx = 0.05
		val dy = 0.05

		val ly = 1.0
		val lx = 1.0

		val x = 0.0 arrangeTo lx + dx withStep dx
		val y = 0.0 arrangeTo ly + dy withStep dy

		val labPath = "src/main/kotlin/sem2/lab7"

		File(labPath / "x").writeText(x.joinToString(" "))
		File(labPath / "y").writeText(y.joinToString(" "))

		val epsilon = 0.0001
		val c = 0.5

		val liebman = liebmanMethod(x, y, epsilon)
		File(labPath / "liebman").writeText(
			liebman.array.joinToString("\n") { it.joinToString(" ") }
		)

		val relaxation = relaxationMethod(x, y, epsilon, c)
		File(labPath / "relaxation").writeText(
			relaxation.array.joinToString("\n") { it.joinToString(" ") }
		)

		val zeidel = zeidelMethod(x, y, epsilon, c)
		File(labPath / "zeidel").writeText(
			zeidel.array.joinToString("\n") { it.joinToString(" ") }
		)

		//Нет поддержки для запуска в Windows
		val process = Runtime.getRuntime().exec("python3 ${labPath / "lab7.py"}")

		val scanner = Scanner(process.errorStream)
		while (scanner.hasNextLine()) println(scanner.nextLine())
	}
}

fun main() = Lab7()()