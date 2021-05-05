package lab1

import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.sqrt

data class Matrix (val array: MutableList<MutableList<Double>>) {

	init {
		if (array.isEmpty() || array.all { it.isEmpty() })
			throw IllegalStateException("lab1.Matrix can not be empty")

		if (!array.all { it.size == array[0].size })
			throw IllegalStateException("lab1.Matrix has different column size")
	}

	companion object {
		fun getIdentityMatrix(n: Int): Matrix {
			val newElementsList = mutableListOf<MutableList<Double>>()
			for (i in 0 until n) {
				val newElementsRow = mutableListOf<Double>()
				for (j in 0 until n) {
					newElementsRow.add(if (i == j) 1.0 else 0.0)
				}
				newElementsList.add(newElementsRow)
			}

			return Matrix(newElementsList)
		}

		fun getEmptyMatrix(rows: Int, columns: Int) = Matrix( MutableList(rows) { MutableList(columns) { 0.0 } })
		fun getEmptyMatrix(rows: Int) = Matrix( MutableList(rows) { MutableList(rows) { 0.0 } })

		fun of(input: Array<Array<Double>>): Matrix = Matrix(input.map { it.toMutableList() }.toMutableList())
		fun of(input: Array<Array<Int>>): Matrix = Matrix(
			input.map { row -> row.map { it.toDouble() }.toMutableList() }.toMutableList()
		)
		fun of(input: List<List<Number>>): Matrix = Matrix(
			input.map { row -> row.map { it.toDouble() }.toMutableList() }.toMutableList()
		)
	}

	val rows = array.size
	val columns = array[0].size

	val isSquare = rows == columns

	val det: Double by lazy {
		if (!isSquare) throw IllegalStateException("Can't calculate determinant for nonsquare matrix")

		val newElements = array.map { it.toMutableList() }.toMutableList()

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

		round(det)
	}

	val norm: Double by lazy { sqrt(array.flatten().sumByDouble { it * it }) }

	operator fun get(index: Int) = array[index].map { it }
	operator fun get(i: Int, j: Int) = array[i][j]

	operator fun set(index: Int, array: List<Double>) = this.array.set(index, array.toMutableList())
	operator fun set(i: Int, j: Int, element: Double) = this.array[i].set(j, element)

	operator fun plus(other: Matrix): Matrix {
		if (rows != other.rows || columns != other.columns)
			throw IllegalStateException("Can't sum 2 matrices with different dimens")

		val outputArray = mutableListOf<MutableList<Double>>()

		for (i in 0 until rows) {
			val newRow = mutableListOf<Double>()
			for (j in 0 until columns)
				newRow.add(this[i, j] + other[i, j])
			outputArray.add(newRow)
		}

		return Matrix(outputArray)
	}

	operator fun minus(other: Matrix): Matrix {
		if (rows != other.rows || columns != other.columns)
			throw IllegalStateException("Can't minus 2 matrices with different dimens")

		val outputArray = mutableListOf<MutableList<Double>>()

		for (i in 0 until rows) {
			val newRow = mutableListOf<Double>()
			for (j in 0 until columns)
				newRow.add(this[i, j] - other[i, j])
			outputArray.add(newRow)
		}

		return Matrix(outputArray)
	}

	operator fun times(other: Matrix): Matrix {
		if (columns != other.rows) throw IllegalStateException("Matrices has inappropriate dimens for times operation")

		val outputArray = mutableListOf<MutableList<Double>>()

		for (i in 0 until rows) {
			val newRow = mutableListOf<Double>()
			for (j in 0 until other.columns) {
				var sum = 0.0
				for (k in 0 until columns) {
					sum += this[i, k] * other[k, j]
				}
				newRow.add(sum)
			}
			outputArray.add(newRow)
		}

		return Matrix(outputArray)
	}

	operator fun times(other: Number): Matrix = Matrix (
		array.asSequence().flatten()
			.map { it * other.toDouble() }.chunked(columns)
			.map { it.toMutableList() }.toMutableList()
	)

	fun getColumn(index: Int): List<Double> {
		val mList = mutableListOf<Double>()
		for (i in 0 until rows)
			mList.add(array[i][index])
		return mList.toList()
	}

	fun getDiagonal(): List<Double> {
		val mList = mutableListOf<Double>()
		array.forEachIndexed { i, mutableList ->
			mutableList.forEachIndexed { j, d ->
				if (i == j) mList.add(d)
			}
		}
		return mList.toList()
	}

	fun transposed(): Matrix {
		val newElementsList = mutableListOf<MutableList<Double>>()

		for (j in 0 until columns) {
			val newElementsRow = mutableListOf<Double>()
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

		val castedElements = array.map { it.toMutableList() }.toMutableList()

		var temp: Double
		val E = getIdentityMatrix(rows)
		for (k in 0 until rows) {
			temp = castedElements[k][k]
			for (j in 0 until rows) {
				castedElements[k][j] /= temp
				E[k, j] /= temp
			}
			for (i in (k + 1) until rows) {
				temp = castedElements[i][k]
				for (j in 0 until rows) {
					castedElements[i][j] -= castedElements[k][j] * temp
					E[i, j] -= E[k][j] * temp
				}
			}
		}
		for (k in (rows - 1) downTo 1) {
			for (i in (k - 1) downTo 0) {
				temp = castedElements[i][k]
				for (j in 0 until rows) {
					castedElements[i][j] -= castedElements[k][j] * temp
					E[i, j] -= E[k, j] * temp
				}
			}
		}
		return E
	}

	infix fun concat(other: Matrix): Matrix {
		if (rows != other.rows) throw IllegalArgumentException("Matrices must have equal row size")

		val output = mutableListOf<List<Double>>()
		array.forEachIndexed { index, mutableList -> output.add(mutableList + other[index]) }
		return of(output)
	}

	fun apply(operation: (Double) -> Double) = of(array.flatten().map { operation(it) }.chunked(columns))

	fun toPrint(): String =
		array.joinToString("\n") { row ->
			row.joinToString("\t")
		}
}