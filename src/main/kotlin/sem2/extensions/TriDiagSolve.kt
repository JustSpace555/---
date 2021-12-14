package sem2.extensions

fun solveTriDiagMethod(
	vecA: List<Double>,
	vecB: List<Double>,
	vecC: List<Double>,
	vecD: List<Double>
): List<Double> {
	val p = MutableList(vecD.size) { 0.0 }.apply { set(0, -vecC[0] / vecB[0]) }
	val q = MutableList(vecD.size) { 0.0 }.apply { set(0, vecD[0] / vecB[0]) }

	for (i in 1 until vecD.lastIndex) {
		val calc = vecB[i] + vecA[i - 1] * p[i - 1]
		p[i] = -vecC[i] / calc
		q[i] = (vecD[i] - vecA[i - 1] * q[i - 1]) / calc
	}

	q[q.lastIndex] =
		(vecD.last() - vecA.last() * q[q.lastIndex - 1]) / (vecB.last() + vecA.last() * p[p.lastIndex - 1])

	val result = MutableList(vecD.size) { 0.0 }.apply { set(lastIndex, q.last()) }
	for (i in q.lastIndex - 1 downTo 0) result[i] = p[i] * result[i + 1] + q[i]

	return result
}