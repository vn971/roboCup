package code.comet

import net.liftweb.actor._
import net.liftweb.http._

object TimeStartSingleton extends LiftActor with ListenerManager {

	private var time = 0L

	def createUpdate = time

	override def lowPriority = {
		case newTime: Long => time = newTime; updateListeners()
	}
}
