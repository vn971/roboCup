package code.comet

import code.comet.TournamentStatus._
import net.liftweb.http.{ CometActor, CometListener }
import ru.ya.vn91.robotour.Constants._
import scala.xml.Text

class GlobalStatus extends CometActor with CometListener {

	private var status: Status = Undefined

	def registerWith = GlobalStatusSingleton

	override def lowPriority = {
		case newStatus: Status => status = newStatus; reRender()
	}

	def render = Text(status match {
		case Undefined => "waiting (tournament not assigned yet)"
		case RegistrationAssigned(time) => s"tournament starts at ${timeLongToString(time + registrationMillis)}, registration starts $registrationHours hours earlier"
		case RegistrationInProgress(regClose) => "registration in progress (until "+timeLongToHours(regClose)+")"
		case GamePlaying(roundNumber) => "playing games" // (round N)
		case WaitingForNextTour(time) => "waiting for next tour at "+timeLongToHours(time)
		case FinishedWithWinner(winner) => "tournament finished. Winner: "+winner+"!"
		case FinishedWithWinners(winners) => "tournament finished. Winners: "+winners.mkString(", ")+"!"
		case FinishedWithDraw => "tournament finished with draw!"
		case ErrorStatus(reason) => "error in server: "+reason
		case CustomStatus(msg) => msg
		case _ => "???"
	})
}
