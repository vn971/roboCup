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
		case Undefined => "waiting (tournament not assigned yet)."
		case RegistrationAssigned(time) => "waiting for registration start at "+timeLongToString(time)+"."
		case RegistrationInProgress(regStart, gameStart) =>
			"registration in progress (opened at "+timeLongToString(regStart)+
				"and closing at "+timeLongToString(gameStart)+")."
		case GamePlaying(rountNumber) => "Playing games." // (round N)
		case WaitingForNextTour(time) => "waiting for next tour at "+timeLongToString(time)+"."
		case Finished(winner) => "tournament finished. Winner: "+winner+"!"
		case _ => "???"
	})
}
