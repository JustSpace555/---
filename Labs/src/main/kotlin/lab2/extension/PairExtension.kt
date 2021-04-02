package lab2.extension

operator fun Pair<Double, Double>.minus(other: Pair<Double, Double>): Pair<Double, Double> =
	Pair(first - other.first, second - other.second)