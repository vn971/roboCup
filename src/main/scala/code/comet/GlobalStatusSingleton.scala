package code
package comet

import net.liftweb.http._
import net.liftweb.actor._
import java.text.SimpleDateFormat
import java.util.TimeZone
import status._

package status {
	sealed class Status
	case object Undefined extends Status
	case class RegistrationAssigned(val time: Long) extends Status
	case class RegistrationInProgress(val regStart: Long, val gameStart: Long) extends Status
	case class GamePlaying(roundNumber: Int) extends Status
	case class WaitingForNextTour(val time: Long) extends Status
	case class Finished(val winner: String) extends Status
}

object GlobalStatusSingleton extends LiftActor with ListenerManager {

	private var status: Status = Undefined

	def createUpdate = status

	override def lowPriority = {
		case newStatus: Status => status = newStatus; updateListeners()
	}
}
