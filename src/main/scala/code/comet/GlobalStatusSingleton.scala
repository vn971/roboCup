package code.comet

import net.liftweb.http._
import net.liftweb.actor._
import java.text.SimpleDateFormat
import java.util.TimeZone
import code.comet.status._
import ru.ya.vn91.robotour.Constants._

object GlobalStatusSingleton extends LiftActor with ListenerManager {

	private var status: Status = Undefined

	def createUpdate = status

	override def lowPriority = {
		case Undefined =>
			status = Undefined
			updateListeners()
		case s: RegistrationAssigned =>
			status = s
			updateListeners()
			ChatServer ! MessageFromAdmin("Tournament start assigned!")
			ChatServer ! MessageFromAdmin("Registration starts at: "+timeLongToString(s.time))
			ChatServer ! MessageFromAdmin("First round (start of games): "+timeLongToString(s.time + registrationLength))
		case s: RegistrationInProgress =>
			status = s
			updateListeners()
			ChatServer ! MessageFromAdmin("Registration opened!")
		case s: GamePlaying =>
			status = s
			updateListeners()
			ChatServer ! MessageFromAdmin("Started next round!")
		case s: WaitingForNextTour =>
			status = s
			updateListeners()
			ChatServer ! MessageFromAdmin("Next round will start in "+(tourBrakeTime / 1000 / 60)+" minutes.")
		case s: FinishedWithWinner =>
			status = s
			updateListeners()
			ChatServer ! MessageFromAdmin("Tournament finished!")
			ChatServer ! MessageFromAdmin("Winner: "+s.winner)
		case FinishedWithDraw =>
			status = FinishedWithDraw
			updateListeners()
			ChatServer ! MessageFromAdmin("Tournament finished!")
			ChatServer ! MessageFromAdmin("Result: draw!")
		case s: CustomStatus =>
			status = s
			updateListeners()
		case s: ErrorStatus =>
			status = s
			updateListeners()
			ChatServer ! MessageFromAdmin("В серверном обработчике турнира произошла ошибка: "+s.reason)
		case any: Status =>
			status = any
			updateListeners()
	}
}

package status {
	sealed class Status
	case object Undefined extends Status
	case class RegistrationAssigned(val time: Long) extends Status
	case class RegistrationInProgress(val regClose: Long) extends Status
	case class GamePlaying(roundNumber: Int) extends Status
	case class WaitingForNextTour(val time: Long) extends Status
	case class FinishedWithWinner(val winner: String) extends Status
	case object FinishedWithDraw extends Status
	case class ErrorStatus(val reason: String) extends Status
	case class CustomStatus(val msg: String) extends Status
}

