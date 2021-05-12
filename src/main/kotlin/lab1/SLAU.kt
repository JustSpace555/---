package lab1

import java.lang.IllegalStateException
import kotlin.math.*
import kotlin.properties.Delegates

class SLAU(private val matrix: Matrix) {

	fun luDecomposition(): Pair<Matrix, Matrix> {
		if (!matrix.isSquare) throw IllegalStateException("lab1.Matrix is not square")

		val lowerMatrix = MutableList(matrix.rows) { MutableList(matrix.columns) { 0.0 } }
		val upperMatrix = MutableList(matrix.rows) { MutableList(matrix.columns) { 0.0 } }

		for (i in 0 until matrix.rows) {

			// Верхняя Треугольная
			for (k in i until matrix.rows) {

				// Суммирование L (i, j) * U (j, k)
				var sum = 0.0
				for (j in 0 until i) sum += lowerMatrix[i][j] * upperMatrix[j][k]

				// Оцениваем U (i, k)
				upperMatrix[i][k] = matrix[i][k] - sum
			}

			// Нижняя Треугольная
			for (k in i until matrix.rows) {

				// Диагональ как 1
				if (i == k) {
					lowerMatrix[i][i] = 1.0
					continue
				}

				// Суммирование L (k, j) * U (j, i)
				var sum = 0.0
				for (j in 0 until i) sum += lowerMatrix[k][j] * upperMatrix[j][i]

				// Оцениваем L (k, i)
				lowerMatrix[k][i] = (matrix[k][i] - sum) / upperMatrix[i][i]
			}
		}

		return Pair(Matrix(lowerMatrix), Matrix(upperMatrix))
	}



	fun runMethod(): Triple<List<Double>, List<Double>, List<Double>> {

		val aIndex = 0
		val bIndex = 1
		val cIndex = 2
		val dIndex = 3

		val pList = mutableListOf(-matrix[0][cIndex] / matrix[0][bIndex])
		val qList = mutableListOf(matrix[0][dIndex] / matrix[0][bIndex])

		for (i in 1 until matrix.rows) {
			pList.add(-matrix[i][cIndex] / (matrix[i][bIndex] + matrix[i][aIndex] * pList[i - 1]))
			qList.add(
				(matrix[i][dIndex] - matrix[i][aIndex] * qList[i - 1]) /
				(matrix[i][bIndex] + matrix[i][aIndex] * pList[i - 1])
			)
		}

		val xList = MutableList(matrix.rows + 1) { 0.0 }
		for (i in matrix.rows - 1 downTo 0)
			xList[i] = pList[i] * xList[i + 1] + qList[i]

		return Triple(pList, qList, xList.slice(0 until matrix.rows).reversed())
	}



	private inner class Exercise3(private val inputEps: Double) {

		private val alpha: Matrix
		private val beta: Matrix

		init {

			if (matrix.rows != matrix.columns - 1)
				throw IllegalStateException("lab1.Matrix should be square for this method")

			val b = mutableListOf<Double>()
			val alphaElements = MutableList(matrix.rows) { MutableList(matrix.columns - 1) { 0.0 } }
			val betaElements = mutableListOf<Double>()

			matrix.array.forEach { b.add(it.last()) }

			for (i in 0 until matrix.rows) {
				betaElements.add(i, b[i] / matrix[i, i])
				for (j in 0 until matrix.columns - 1)
					alphaElements[i][j] = if (i != j) -matrix[i, j] / matrix[i, i] else 0.0
			}

			alpha = Matrix(alphaElements)
			beta = Matrix(mutableListOf(betaElements)).transposed()
		}

		fun easyIterationsMethod(): Triple<Matrix, Int, Double> {

			var x = beta.copy()

			var eps: Double
			var i = 1
			do {
				val m = alpha * x
				val prevX = x.copy()
				x = beta + m
				eps = alpha.norm / (1 - alpha.norm) * (x - prevX).norm
				i++
			} while (eps > inputEps)

			return Triple(x, i, eps)
		}

		fun zeidelMethod(): Triple<Matrix, Int, Double> {

			var x = beta.copy()
			val B = Matrix.getEmptyMatrix(alpha.rows)
			val C = Matrix.getEmptyMatrix(alpha.rows)

			for (i in 0 until alpha.rows)
				for (j in 0 until alpha.rows) {
					if (j < i)
						B[i, j] = alpha[i, j]
					else
						C[i, j] = alpha[i, j]
				}

			val eMinusBRev = (Matrix.getIdentityMatrix(alpha.rows) - B).reverse()
			var i = 1
			var epsI by Delegates.notNull<Double>()

			do {
				val prevX = x.copy()
				x = eMinusBRev * C * prevX + eMinusBRev * beta
				epsI = if (alpha.norm < 1)
					C.norm / (1 - alpha.norm) * (x - prevX).apply(::abs).norm
				else (x - prevX).apply(::abs).norm
				i++
			} while (epsI > inputEps)

			return Triple(x, i, epsI)
		}
	}

	fun easyIterationMethod(epsilon: Double) = Exercise3(epsilon).easyIterationsMethod()
	fun zeidelMethod(epsilon: Double) = Exercise3(epsilon).zeidelMethod()

	fun rotationMethod(epsilon: Double): Pair<Matrix, Matrix> {

		if (!matrix.isSquare) throw IllegalStateException("lab1.Matrix is not square")

		val findMax: Matrix.() -> Pair<Int, Int> = {
			var max = abs(array[0][0])
			var maxIJ = Pair(0, 0)

			array.forEachIndexed { i, mutableList ->
				mutableList.forEachIndexed { j, d ->
					if (max < abs(d)) {
						max = abs(d)
						maxIJ = Pair(i, j)
					}
				}
			}
			maxIJ
		}

		val lambda = Matrix.getEmptyMatrix(1, matrix.rows)
		var X = Matrix.getIdentityMatrix(matrix.rows)
		var A = matrix.copy()
		var p: Double

		val squareSum: Matrix.() -> Double = {
			var sum = 0.0
			for (i in 1 until rows)
				for (j in 0 until i)
					sum += this[i, j].pow(2)

			sqrt(sum)
		}

		do {
			val maxIJ = A.findMax()
			val angle = if (A[maxIJ.first, maxIJ.first] == A[maxIJ.second, maxIJ.second])
				PI / 4
			else
				0.5 * atan(
					2 * A[maxIJ.first, maxIJ.second] / (A[maxIJ.first, maxIJ.first] - A[maxIJ.second, maxIJ.second])
				)

			val sin = sin(angle)
			val cos = cos(angle)

			val U = Matrix.getIdentityMatrix(matrix.rows)
			U[maxIJ.first, maxIJ.first] = cos
			U[maxIJ.first, maxIJ.second] = -sin
			U[maxIJ.second, maxIJ.first] = sin
			U[maxIJ.second, maxIJ.second] = cos

			X *= U
			A = U.transposed() * A * U
			p = A.squareSum()
		} while (p > epsilon && angle != 0.0)

		for (i in 0 until A.rows) lambda[0, i] = A[i, i]

		return Pair(X, lambda)
	}

	fun qrDecomposition(matrix: Matrix): Pair<Matrix, Matrix> {

		if (!matrix.isSquare) throw IllegalStateException("lab1.Matrix is not square")

		var R = matrix.copy()
		var Q = Matrix.getIdentityMatrix(matrix.rows)
		val V = MutableList(matrix.rows) { 0.0 }

		for (k in 0 until matrix.rows - 1) {
			for (i in 0 until matrix.rows) {
				when {
					i < k -> V[i] = 0.0
					i == k -> {
						var norm = 0.0
						val column = R.getColumn(k)
						for (j in k until matrix.rows) norm += column[j].pow(2)
						norm = sqrt(norm)

						V[i] = R[i, i] + sign(R[i, i]) * norm
					}
					else -> V[i] = R[i, k]
				}
			}

			val temp = Matrix.getEmptyMatrix(matrix.rows)
			var p = 0.0
			for (i in 0 until matrix.rows) {
				for (j in 0 until matrix.rows)
					temp[i, j] = V[i] * V[j]
				p += V[i].pow(2)
			}
			p = -2 / p
			val H = Matrix.getIdentityMatrix(matrix.rows) + temp * p
			R = H * R
			Q *= H
		}
		return Pair(Q, R)
	}

	fun exercise5(epsilon: Double): Pair<List<Double>, Int> {

		if (!matrix.isSquare) throw IllegalStateException("lab1.Matrix is not square")

		val finish: (Matrix, Double) -> Boolean = finish@{ lMatrix, lEpsilon ->

			val squareColumnSum: Matrix.(Int, Int) -> Double = { columnNumber, firstIndex ->
				var sum = 0.0
				for (i in firstIndex until columns) sum += this[i, columnNumber].pow(2)
				sum
			}

			var sum1: Double
			var sum2: Double
			for (j in 0 until lMatrix.rows) {
				sum1 = lMatrix.squareColumnSum(j, j + 1)
				sum2 = lMatrix.squareColumnSum(j, j + 2)
				when {
					sum2 > lEpsilon -> return@finish false
					sum1 > lEpsilon -> {
						if (j == 0) return@finish false
						continue
					}
				}
			}
			true
		}

		var k = 0
		var A = matrix.copy()

		do {
			val qr = qrDecomposition(A)
			A = qr.second * qr.first
			k++
		} while (!finish(A, epsilon))

		return Pair(A.getDiagonal(), k)
	}
}