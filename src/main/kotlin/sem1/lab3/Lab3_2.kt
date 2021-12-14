package sem1.lab3

import sem1.lab1.Matrix
import sem1.lab1.SLAU
import kotlin.math.pow

object Lab3_2 {

	private const val xStar = 0.8
	private val x = listOf(0.0, 0.5, 1.0, 1.5, 2.0)
	private val y = listOf(0.0, 0.97943, 1.8415, 2.4975, 2.9093)

	operator fun invoke() {

		println("Exercise 3.2")

		val A = Matrix.of(MutableList(x.size - 2) { MutableList(x.size - 2) { 0.0 } })
		val B = Matrix.of(MutableList(x.size - 2) { MutableList(1) { 0.0 } })

		val hi: (Int) -> Double = { i -> x[i] - x[i - 1] }

		A[0, 0] = 2 * (hi(1) + hi(2))
		A[0, 1] = hi(2)
		B[0, 0] = 3 * ((y[2] - y[1]) / hi(2) - (y[1] - y[0]) / hi(1))
		var j = 0
		for (i in 3 until x.lastIndex) {
			A[i - 2, j] = hi(i - 1)
			A[i - 2, j + 1] = 2 * (hi(i - 1) + hi(i))
			A[i - 2, j + 2] = hi(i)
			B[i - 2, 0] = 3 * ((y[i] - y[i - 1]) / hi(i) - (y[i - 1] - y[i - 2]) / hi(i - 1))
			j++
		}

		A[x.lastIndex - 2, j] = hi(x.lastIndex - 1)
		A[x.lastIndex - 2, j + 1] = 2 * (hi(x.lastIndex - 1) + hi(x.lastIndex))
		B[x.lastIndex - 2, 0] = 3 * (
				(y[x.lastIndex] - y[x.lastIndex - 1]) / hi(x.lastIndex) -
				(y[x.lastIndex - 1] - y[x.lastIndex - 2]) / hi(x.lastIndex - 1)
		)

		val c = listOf(0.0) + SLAU(A concat B).zeidelMethod(0.00001).first.transposed()[0]

		val a = mutableListOf<Double>()
		val b = mutableListOf<Double>()
		val d = mutableListOf<Double>()

		for (i in 1 until x.lastIndex) {
			a.add(i - 1, y[i - 1])
			b.add(i - 1, (y[i] - y[i - 1]) / hi(i) - (hi(i) * (c[i] + 2 * c[i - 1])) / 3)
			d.add(i - 1, (c[i] - c[i - 1]) / (3 * hi(i)))
		}

		a.add(y[x.lastIndex - 1])
		b.add(
				(y[x.lastIndex] - y[x.lastIndex - 1]) / hi(x.lastIndex - 1) -
				2 * hi(x.lastIndex - 1) * c[x.lastIndex - 1] / 3
		)
		d.add(-c[x.lastIndex - 1] / (3 * hi(x.lastIndex - 1)))

		val f: (Int) -> Double = { index ->
			val xi = xStar - x[index]
			a[index] + b[index] * xi + c[index] * xi.pow(2) + d[index] * xi.pow(3)
		}

		with(x.windowed(2)) {
			val index = indexOf(find { xStar > it[0] && xStar <= it[1] })
			forEachIndexed { i, pair ->
				println("i = ${i + 1}, [x_$i, x_${i + 1}] = $pair, " +
						"a_i = ${a[i]}, b_i = ${b[i]}, c_i = ${c[i]}, d_i = ${d[i]}"
				)
			}
			val xi = x[index]
			println("f(x) = ${a[index]} + ${b[index]} * (x - $xi) + " +
					"${c[index]} * (x - $xi)^2 + ${d[index]} * (x - $xi)^3"
			)
			println("f($xStar) = ${f(index)}")
			println("-------------------------------------------------------\n")
		}
	}
}