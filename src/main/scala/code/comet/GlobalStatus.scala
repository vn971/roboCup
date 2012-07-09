package code.comet

//import net.liftweb.http._
import net.liftweb.http.{CometActor, CometListener}
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
		case RegistrationInProgress(regClose) => "registration in progress (until "+timeLongToHours(regClose)+")"
		case GamePlaying(roundNumber) => "playing games" // (round N)
		case WaitingForNextTour(time) => "waiting for next tour at "+timeLongToHours(time)
		case FinishedWithWinner(winner) => "tournament finished. Winner: "+winner+"!"
		case FinishedWithDraw => "tournament finished with draw!"
		case ErrorStatus(reason) => "error in server: "+reason
		case _ => "???"
	})
}
