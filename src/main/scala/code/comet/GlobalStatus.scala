package code
package comet

import net.liftweb.http._
import java.util.Date
import ru.ya.vn91.robotour.Constants._
import status._
import ru.ya.vn91.robotour.Constants._

class GlobalStatus extends CometActor with CometListener {

	private var status: Status = Undefined

	def registerWith = GlobalStatusSingleton

	override def lowPriority = {
		case newStatus: Status => status = newStatus; reRender()
	}

	def render = "*" #> (status match {
		case Undefined => "waiting (tournament not assigned yet)"
		case RegistrationAssigned(time) => "waiting for registration ("+timeLongToString(time)+")"
		case RegistrationInProgress(regClose) => "registration in progress (until "+timeLongToString(regClose)+")"
		case GamePlaying(rountNumber) => "playing games" // (round N)
		case WaitingForNextTour(time) => "waiting for next tour at "+timeLongToString(time)
		case FinishedWithWinner(winner) => "tournament finished. Winner: "+winner+"!"
		case FinishedWithDraw => "tournament finished with draw!"
		case code.comet.status.Error(reason) => "error in server: "+reason
		case _ => "???"
	})
}
