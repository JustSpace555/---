package lab3.intergration5

internal data class Dot(
		val i: Int,
		val xi: Double,
		val yi: Double,
		val rectangleMethod: Double,
		val trapezoidMethod: Double,
		val simpsonMethod: Double
) {
	override fun toString(): String = "i = $i, x_i = $xi, y_i = $yi, rectangle = $rectangleMethod, " +
			"trapezoid = $trapezoidMethod, Simpson = $simpsonMethod"
}