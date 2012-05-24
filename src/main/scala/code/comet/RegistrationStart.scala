package code
package comet

import net.liftweb.http._

class RegistrationStart extends CometActor with CometListener {

	private var time = "undefined"

	def registerWith = RegistrationStartSingleton

	override def lowPriority = {
		case s: String => time = s; reRender()
	}

	def render = "*" #> time
}
