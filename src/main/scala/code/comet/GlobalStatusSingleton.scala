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
			ChatServer ! MessageToChatServer("Assigned tournament start!")
			ChatServer ! MessageToChatServer(s"Registration starts at: ${timeLongToString(s.time)} Moscow Time")
			ChatServer ! MessageToChatServer(
				"First round (start of games): " +
					timeLongToString(s.time + registrationPeriod.toMillis))
		case s: RegistrationInProgress =>
			status = s
			updateListeners()
			ChatServer ! MessageToChatServer("Registration opened!")
		case s: GamePlaying =>
			status = s
			updateListeners()
			ChatServer ! MessageToChatServer("Started next round!")
		case s: WaitingForNextTour =>
			status = s
			updateListeners()
			ChatServer ! MessageToChatServer("Next round will start at " + timeLongToHours(s.time))
		case s: FinishedWithWinner =>
			status = s
			updateListeners()
			ChatServer ! MessageToChatServer("Tournament finished!")
			ChatServer ! MessageToChatServer("Winner: " + s.winner)
		case s: FinishedWithWinners =>
			status = s
			updateListeners()
			ChatServer ! MessageToChatServer("Tournament finished!")
			ChatServer ! MessageToChatServer("Winners: " + s.winners.mkString(", "))
		case FinishedWithDraw =>
			status = FinishedWithDraw
			updateListeners()
			ChatServer ! MessageToChatServer("Tournament finished!")
			ChatServer ! MessageToChatServer("Result: draw!")
		case s: CustomStatus =>
			status = s
			updateListeners()
		case s: ErrorStatus =>
			status = s
			updateListeners()
			ChatServer ! MessageToChatServer("В серверном обработчике турнира произошла ошибка: " + s.reason)
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

