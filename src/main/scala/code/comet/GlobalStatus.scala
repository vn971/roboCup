package code.comet

import code.comet.TournamentStatus._
import net.liftweb.http.{ CometActor, CometListener }
import ru.ya.vn91.robotour.Constants._
import scala.xml.Unparsed

class GlobalStatus extends CometActor with CometListener {

	private var status: Status = Undefined

	def registerWith = GlobalStatusSingleton

	override def lowPriority = {
		case newStatus: Status => status = newStatus; reRender()
	}

	def render = status match {
		case Undefined => "*" #> "waiting (tournament not assigned yet)"
		case RegistrationAssigned(time) => "*" #>
			s"tournament starts at ${timeLongToString(time + registrationTime.toMillis)}, registration starts ${registrationTime.toHours} hours earlier"
		case RegistrationInProgress(regClose) => "*" #> s"registration in progress (until ${timeLongToHours(regClose)})"
		case GamePlaying(roundNumber) => "*" #> "playing games" // (round N)
		case WaitingForNextTour(time) => "*" #> s"waiting for next tour at ${timeLongToHours(time)}"
		case FinishedWithWinner(winner) => "*" #> s"tournament finished. Winner: $winner!"
		case FinishedWithWinners(winners) => "*" #> s"tournament finished. Winners: ${winners.mkString(", ")}!"
		case FinishedWithDraw => "*" #> "tournament finished with draw!"
		case ErrorStatus(reason) => "*" #> s"error in server: $reason"
		case CustomStatus(msg) => "*" #> Unparsed(msg)
		case _ => "*" #> "???"
	}
}
