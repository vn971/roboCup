package code
package comet

import net.liftweb.http._
import java.util.Date
import ru.ya.vn91.robotour.Constants._

class TimeStart extends CometActor with CometListener {
	private var time = 0L
	def registerWith = TimeStartSingleton
	override def lowPriority = {
		case newTime: Long => time = newTime; reRender()
	}
	def longToString(long: Long) = TimeStartSingleton.simpleDateFormat.format(new Date(long))
	def render = "*" #> (if (time > 0) longToString(time - registrationLength) else "undefined yet"+sys.props.get("run.mode").get)
}

class GamesStart extends CometActor with CometListener {
	private var time = 0L
	def registerWith = TimeStartSingleton
	override def lowPriority = {
		case newTime: Long => time = newTime; reRender()
	}
	def longToString(long: Long) = TimeStartSingleton.simpleDateFormat.format(new Date(long))
	def render = "*" #> (if (time > 0) longToString(time) else "undefined yet")
}
