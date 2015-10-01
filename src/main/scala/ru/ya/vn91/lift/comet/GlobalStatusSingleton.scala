package ru.ya.vn91.lift.comet

import ru.ya.vn91.lift.comet.TournamentStatus._
import net.liftweb.actor._
import net.liftweb.http._
import ru.ya.vn91.robotour.Constants._
import ru.ya.vn91.robotour.Core

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
			Core.chatServer ! MessageToChatServer("Assigned tournament start!")
			Core.chatServer ! MessageToChatServer(s"Registration starts at: ${timeLongToString(s.time)} Moscow Time")
			Core.chatServer ! MessageToChatServer(
				"First round (start of games): " +
					timeLongToString(s.time + registrationPeriod.toMillis))
		case s: RegistrationInProgress =>
			status = s
			updateListeners()
			Core.chatServer ! MessageToChatServer("Registration opened!")
		case s: GamePlaying =>
			status = s
			updateListeners()
			Core.chatServer ! MessageToChatServer("Started next round!")
		case s: WaitingForNextTour =>
			status = s
			updateListeners()
			Core.chatServer ! MessageToChatServer("Next round will start at " + timeLongToHours(s.time))
		case s: FinishedWithWinner =>
			status = s
			updateListeners()
			Core.chatServer ! MessageToChatServer("Tournament finished!")
			Core.chatServer ! MessageToChatServer("Winner: " + s.winner)
		case s: FinishedWithWinners =>
			status = s
			updateListeners()
			Core.chatServer ! MessageToChatServer("Tournament finished!")
			Core.chatServer ! MessageToChatServer("Winners: " + s.winners.mkString(", "))
		case FinishedWithDraw =>
			status = FinishedWithDraw
			updateListeners()
			Core.chatServer ! MessageToChatServer("Tournament finished!")
			Core.chatServer ! MessageToChatServer("Result: draw!")
		case s: CustomStatus =>
			status = s
			updateListeners()
		case s: ErrorStatus =>
			status = s
			updateListeners()
			Core.chatServer ! MessageToChatServer("В серверном обработчике турнира произошла ошибка: " + s.reason)
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

