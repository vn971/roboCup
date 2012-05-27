package code
package comet

import net.liftweb.http._
import java.util.Date

class RegistrationStart extends CometActor with CometListener {

	private var time = 0L

	def registerWith = RegistrationStartSingleton

	override def lowPriority = {
		case newTime: Long => time = newTime; reRender()
	}

	def longToString(long: Long) = RegistrationStartSingleton.simpleDateFormat.format(new Date(long))

	def render = "*" #> (if (time > 0) longToString(time) else "undefined yet")
}

class GamesStart extends CometActor with CometListener {

	private var time = 0L

	def registerWith = RegistrationStartSingleton

	override def lowPriority = {
		case newTime: Long => time = newTime; reRender()
	}

	def longToString(long: Long) = RegistrationStartSingleton.simpleDateFormat.format(new Date(long))

	def render = "*" #> (if (time > 0) longToString(time + 1000L * 60 * 60 * 3) else "undefined yet")
}
