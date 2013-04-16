package code.comet

import code.comet.TournamentStatus._
import net.liftweb.actor._
import net.liftweb.http._
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
			ChatServer ! MessageFromAdmin("Assigned tournament start!")
			ChatServer ! MessageFromAdmin("Registration starts at: "+timeLongToString(s.time))
			ChatServer ! MessageFromAdmin("First round (start of games): "+timeLongToString(s.time + registrationMillis))
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
			ChatServer ! MessageFromAdmin("Next round will start at "+timeLongToHours(s.time))
		case s: FinishedWithWinner =>
		status = s
		updateListeners()
		ChatServer ! MessageFromAdmin("Tournament finished!")
		ChatServer ! MessageFromAdmin("Winner: "+s.winner)
		case s: FinishedWithWinners =>
			status = s
			updateListeners()
			ChatServer ! MessageFromAdmin("Tournament finished!")
			ChatServer ! MessageFromAdmin("Winners: "+s.winners.mkString(", "))
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

object TournamentStatus {
	sealed class Status
	case object Undefined extends Status
	case class RegistrationAssigned(time: Long) extends Status
	case class RegistrationInProgress(regClose: Long) extends Status
	case class GamePlaying(roundNumber: Int) extends Status
	case class WaitingForNextTour(time: Long) extends Status
	case class FinishedWithWinner(winner: String) extends Status
	case class FinishedWithWinners(winners: List[String]) extends Status
	case object FinishedWithDraw extends Status
	case class ErrorStatus(reason: String) extends Status
	case class CustomStatus(msg: String) extends Status
}

