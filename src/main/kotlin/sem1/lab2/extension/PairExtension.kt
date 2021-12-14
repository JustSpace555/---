package sem1.lab2.extension

internal operator fun Pair<Double, Double>.minus(other: Pair<Double, Double>): Pair<Double, Double> =
	Pair(first - other.first, second - other.second)