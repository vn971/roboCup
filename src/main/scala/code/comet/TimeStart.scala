package code
package comet

import net.liftweb.http._
import ru.ya.vn91.robotour.Constants._

class TimeStart extends CometActor with CometListener {
	private var time = 0L
	def registerWith = TimeStartSingleton
	override def lowPriority = {
		case newTime: Long => time = newTime; reRender()
	}
	def render = "*" #> (if (time > 0) timeLongToString(time - registrationMillis) else "undefined yet")
}
