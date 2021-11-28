package lab5

import javafx.application.Application
import javafx.stage.Stage

class Plot(
	private val analyticalSolutionDots: List<Double>,
	private val explicitFiniteDifferenceDots: List<Double>,
	private val implicitFiniteDifferenceDots: List<Double>,
	private val crankNickolsonDots: List<Double>
) : Application() {

	override fun start(primaryStage: Stage?) {
		primaryStage?.let {

		}
	}

	fun draw() = launch()
}