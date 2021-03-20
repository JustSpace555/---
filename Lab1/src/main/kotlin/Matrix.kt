import java.lang.IllegalStateException
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round

data class Matrix (private val array: List<List<Number>>) {

	constructor(array: Array<Array<Int>>): this(array.map { it.toList() })
	constructor(array: Array<Array<Double>>): this(array.map { it.toList() })

	init {
		if (!array.all { it.size == array[0].size })
			throw IllegalStateException("Matrix has different column size")
	}

	companion object {
		fun getIdentityMatrix(n: Int): Matrix {
			val newElementsList = mutableListOf<List<Int>>()
			for (i in 0 until n) {
				val newElementsRow = mutableListOf<Int>()
				for (j in 0 until n) {
					newElementsRow.add(if (i == j) 1 else 0)
				}
				newElementsList.add(newElementsRow)
			}

			return Matrix(newElementsList)
		}

		fun getEmptyMatrix(rows: Int, columns: Int) = Matrix( List(rows) { List(columns) { 0 } })
	}

	val rows = array.size
	val columns = array[0].size

	val isSquare = rows == columns

	val det: Double
		get() {
			if (!isSquare) throw IllegalStateException("Can't calculate determinant for nonsquare matrix")

			val newElements = getElementsDouble()

			for (step in 0 until rows - 1)
				for (row in step + 1 until rows) {
					val coefficient = if (newElements[step][step] == 0.0) {
						0.0
					} else {
						-newElements[row][step] / newElements[step][step]
					}

					for (col in step until rows)
						newElements[row][col] += newElements[step][col] * coefficient
				}

			var det = 1.0
			for (i in 0 until rows)
				det *= newElements[i][i]

			return round(det)
		}

	val norm: Double
		get() {
			var max = 0.0
			array.forEach { row ->
				var sum = 0.0
				row.forEach { sum += abs(it.toDouble()) }
				if (max < sum) max = sum
			}
			return max
		}



	operator fun get(index: Int) = array[index].map { it.toDouble() }
	operator fun get(i: Int, j: Int) = array[i][j].toDouble()

	operator fun set(index: Int, array: List<Number>) = this.array.toMutableList().set(index, array)
	operator fun set(i: Int, j: Int, element: Number) = this.array[i].toMutableList().set(j, element)

	operator fun plus(other: Matrix): Matrix {
		if (rows != other.rows || columns != other.columns)
			throw IllegalStateException("Can't sum 2 matrices with different dimens")

		val outputArray = mutableListOf<MutableList<Number>>()

		for (i in 0 until rows) {
			val newRow = mutableListOf<Number>()
			for (j in 0 until columns)
				newRow.add((this[i][j] + other[i][j]).tryCastToInt())
			outputArray.add(newRow)
		}

		return Matrix(outputArray)
	}

	operator fun minus(other: Matrix): Matrix {
		if (rows != other.rows || columns != other.columns)
			throw IllegalStateException("Can't minus 2 matrices with different dimens")

		val outputArray = mutableListOf<MutableList<Number>>()

		for (i in 0 until rows) {
			val newRow = mutableListOf<Number>()
			for (j in 0 until columns)
				newRow.add((this[i][j] - other[i][j]).tryCastToInt())
			outputArray.add(newRow)
		}

		return Matrix(outputArray)
	}

	operator fun times(other: Matrix): Matrix {
		if (columns != other.rows) throw IllegalStateException("Matrices has inappropriate dimens for times operation")

		val outputArray = mutableListOf<MutableList<Number>>()

		for (i in 0 until rows) {
			val newRow = mutableListOf<Number>()
			for (j in 0 until other.columns) {
				var sum = 0.0
				for (k in 0 until columns) {
					sum += this[i][k] * other[k][j]
				}
				newRow.add(sum)
			}
			outputArray.add(newRow)
		}

		return Matrix(outputArray)
	}

	operator fun times(other: Number): Matrix = Matrix (
		array.flatten().map { (it.toDouble() * other.toDouble()).tryCastToInt() }.chunked(columns)
	)



	fun getElementsDouble() = array.map { row -> row.map { it.toDouble() }.toMutableList() }.toMutableList()

	fun transposed(): Matrix {
		val newElementsList = mutableListOf<List<Number>>()

		for (j in 0 until columns) {
			val newElementsRow = mutableListOf<Number>()
			for (i in 0 until rows) {
				newElementsRow.add(array[i][j])
			}
			newElementsList.add(newElementsRow)
		}

		return Matrix(newElementsList)
	}

	fun reverse(): Matrix {
		when {
			!isSquare -> throw IllegalStateException("Can't find reverse matrix for nonsquare matrix")
			det == 0.0 -> throw IllegalStateException("Can't find reverse matrix for matrix which determinant is zero")
		}

		val castedElements = getElementsDouble()

		var temp: Double
		val E = getIdentityMatrix(4).getElementsDouble()
		for (k in 0 until rows) {
			temp = castedElements[k][k]
			for (j in 0 until rows) {
				castedElements[k][j] /= temp
				E[k][j] /= temp
			}
			for (i in (k + 1) until rows) {
				temp = castedElements[i][k]
				for (j in 0 until rows) {
					castedElements[i][j] -= castedElements[k][j] * temp
					E[i][j] -= E[k][j] * temp
				}
			}
		}
		for (k in (rows - 1) downTo 1) {
			for (i in (k - 1) downTo 0) {
				temp = castedElements[i][k]
				for (j in 0 until rows) {
					castedElements[i][j] -= castedElements[k][j] * temp
					E[i][j] -= E[k][j] * temp
				}
			}
		}
		return Matrix(E)
	}

	fun toPrint(isRound: Boolean = true): String =
		if (isRound) {
			array.joinToString("\n") { row ->
				row.joinToString("\t") { round(it.toDouble()).toString() }
			}
		} else {
			array.joinToString("\n") { row ->
				row.joinToString("\t")
			}
		}

	private fun Number.tryCastToInt(): Number =
		if (this.toDouble() - this.toInt() == 0.0) this.toInt() else this.toDouble()
}