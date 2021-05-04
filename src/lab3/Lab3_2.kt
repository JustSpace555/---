package lab3

import lab1.Matrix
import lab1.SLAU
import kotlin.math.pow

object Lab3_2 {

	private const val xStar = 1.5
			//0.8
	private val x = listOf(
//					0.0, 0.5, 1.0, 1.5, 2.0
					0.0, 1.0, 2.0, 3.0, 4.0
			)
	private val y = listOf(
//			0.0, 0.97943, 1.8415, 2.4975, 2.9093
	0.0, 1.8415,                2.9093,                3.1411,               3.2432
	)

	operator fun invoke() {

		val A = Matrix.of(MutableList(x.size - 2) { MutableList(x.size - 2) { 0.0 } })
		val B = Matrix.of(MutableList(x.size - 2) { MutableList(1) { 0.0 } })

		val hi: (Int) -> Double = { i -> x[i] - x[i - 1] }

//		for (i in 2 .. x.lastIndex) {
//			A[i - 2, 0] = 3 * (
//					(y[i] - y[i - 1]) / hi(i) +
//					(y[i - 1] - y[i - 2]) / hi(i - 1)
//					)
//			B[i - 2, 0] = hi(i - 1)
//			B[i - 2, 1] = 2 * (hi(i - 1) + hi(i))
//			B[i - 2, 2] = hi(i)
//
////			for (j in 0 until B.rows)
////				for (k in 0 until B.columns)
////					B[j, k] = when {
////						j == k -> 2 * (hi(i - 1) + hi(i))
////						j < k && (k != B.columns - 1 || j != 0) -> hi(i)
////						j > k && (j != B.rows - 1 || k != 0) -> hi(i - 1)
////						else -> 0.0
////					}
//		}

		A[0, 0] = 2 * (hi(1) + hi(2))
		A[0, 1] = hi(2)
		B[0, 0] = 3 * ((y[2] - y[1]) / hi(2) - (y[1] - y[0]) / hi(1))
		var j = 0
		for (i in 3 until x.size - 1) {
			A[i - 2, j] = hi(i - 1)
			A[i - 2, j + 1] = 2 * (hi(i - 1) + hi(i))
			A[i - 2, j + 2] = hi(i)
			B[i - 2, 0] = 3 * ((y[i] - y[i - 1]) / hi(i) - (y[i - 1] - y[i - 2]) / hi(i - 1))
			j++
		}

		A[x.size - 3, j] = hi(x.size - 2)
		A[x.size - 3, j + 1] = 2 * (hi(x.size - 2) + hi(x.size - 1))
		B[x.size - 3, 0] = 3 * ((y[x.size - 1] - y[x.size - 2]) / hi(x.size - 1) - (y[x.size - 2] - y[x.size - 3]) / hi(x.size - 2))

		val c = listOf(0.0) + SLAU(A.concat(B)).zeidelMethod(0.00001).first.transposed()[0]

		val a = mutableListOf<Double>()
		val b = mutableListOf<Double>()
		val d = mutableListOf<Double>()

		for (i in 1 until x.size - 1) {
			a.add(i - 1, y[i - 1])
			b.add(i - 1, (y[i] - y[i - 1]) / hi(i) - (hi(i) * (c[i] + 2 * c[i - 1])) / 3)
			d.add(i - 1, (c[i] - c[i - 1]) / (3 * hi(i)))
		}

		a.add(y[x.size - 2])
		b.add((y[x.size - 1] - y[x.size - 2]) / hi(x.size - 2) - 2 * hi(x.size - 2) * c[x.size - 2] / 3)
		d.add(-c[x.size - 2] / (3 * hi(x.size - 2)))

		println(a)
		println(b)
		println(c)
		println(d)

		println(a[1] + b[1] * (xStar - 1) + c[1] * (xStar - 1).pow(2) + d[1] * (xStar - 1).pow(3))

//		val ai: (Int) -> Double = { i -> y[i - 1] }
//		val bi: (Int) -> Double = { i -> (y[i] - y[i - 1]) / hi(i) - 1.0 / 3 * hi(i) * (ci[i + 1] + 2 * ci[i]) }
//		val di: (Int) -> Double = { i -> (ci[i + 1] - ci[i]) / (3 * hi(i)) }
//
//		for (i in 1 .. x.lastIndex) {
//			println(
//					"i = $i, [x_${i - 1}, x_$i] = ${x.windowed(2)[i - 1]}, " +
//					"a_i = ${ai(i)}, b_i = ${bi(i)}, c_i = ${ci[i]}, d_i = ${di(i)}"
//			)
//		}
//
//		val index = x.indexOf(x.windowed(2).find { xStar > it[0] && xStar < it[1] }!!.last())
//		println("\nX* = $xStar => i = $index")
//
//		val fx: (Double, Int) -> Double = { xi, i ->
//			val deltaX: (Double, Int) -> Double = { dx, di -> dx - x[di - 1] }
//			ai(i) + bi(i) * deltaX(xi, i) + ci[i] * deltaX(xi, i).pow(2) + di(i) * deltaX(xi, i).pow(3)
//		}
//
//		println("f($xStar) = ${fx(xStar, index)}")
	}
}