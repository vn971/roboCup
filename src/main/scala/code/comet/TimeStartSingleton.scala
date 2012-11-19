package code
package comet

import net.liftweb.http._
import net.liftweb.actor._
import java.text.SimpleDateFormat
import java.util.TimeZone

object TimeStartSingleton extends LiftActor with ListenerManager {

	private var time = 0L

	def createUpdate = time

	override def lowPriority = {
		case newTime: Long => time = newTime; updateListeners()
	}
}
