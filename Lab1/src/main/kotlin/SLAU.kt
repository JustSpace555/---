import java.lang.IllegalStateException
import java.util.Vector

class SLAU(private val matrix: Matrix) {

	fun luDecomposition(): Pair<Matrix, Matrix> {
		if (!matrix.isSquare) throw IllegalStateException("Matrix is not square")

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
				throw IllegalStateException("Matrix should be square for this method")

			val b = mutableListOf<Double>()
			val alphaElements = MutableList(matrix.rows) { MutableList(matrix.columns - 1) { 0.0 } }
			val betaElements = mutableListOf<Double>()

			matrix.getElementsDouble().forEach { b.add(it.last()) }

			for (i in 0 until matrix.rows) {
				betaElements.add(i, b[i] / matrix[i, i])
				for (j in 0 until matrix.columns - 1)
					alphaElements[i][j] = if (i != j) -matrix[i, j] / matrix[i, i] else 0.0
			}

			alpha = Matrix(alphaElements)
			beta = Matrix(listOf(betaElements)).transposed()
		}

		fun easyIterationsMethod(): Triple<Matrix, Int, Double> {

			var x = beta.copy()
			val alphaNorm = alpha.norm

			var eps: Double
			var i = 1
			do {
				val m = alpha * x
				val prevX = x.copy()
				x = beta + m
				eps = alphaNorm / (1 - alphaNorm) * (x - prevX).norm
				i++
			} while (eps > inputEps)

			return Triple(x, i, eps)
		}

		/*
		fun zeidelMethod(inputEps: Double) {

			if (matrix.rows != matrix.columns - 1) throw IllegalStateException("Matrix should be square for this method")

			val matrixB = Matrix.getEmptyMatrix(matrix.rows, matrix.columns - 1)
			val matrixC = Matrix.getEmptyMatrix(matrix.rows, matrix.columns - 1)

			for (i in 0 until matrix.rows) {
				for (j in 0 until matrix.columns - 1) {
					if (j < i) {
						matrixB[i, j] = alpha[i, j]
						matrixC[i, j] = 0
					} else {
						matrixB[i, j] = 0
						matrixC[i, j] = alpha[i, j]
					}
				}
			}

			for (i in 0 until matrix.rows) {
				for (j in 0 until matrix.columns - 1) {
					if (i == j) matrixB[i, j] = 1
					else if (j < i) matrixB[i, j] *= -1
				}
			}


		}
		 */
	}

	fun easyIterationMethod(epsilon: Double) = Exercise3(epsilon).easyIterationsMethod()
}