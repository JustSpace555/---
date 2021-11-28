package extensions

class Arrange internal constructor(private val from: Double, private val to: Double) {
	infix fun withStep(step: Double): List<Double> {
		val output = mutableListOf<Double>()
		var i = from
		while (i <= to) {
			output.add(i)
			i += step
		}
		return output
	}
}

infix fun Double.arrangeTo(to: Double) = Arrange(this, to)