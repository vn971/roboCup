package code.snippet

import net.liftweb.http.S
import ru.ya.vn91.robotour.Constants
import scala.xml._

object ConstantsSnippet {

	implicit def toNodeSeq(s: String) = Text(s)
	implicit def toNodeSeq(i: Int) = Text(i.toString)
	implicit def toNodeSeq(l: Long) = Text(l.toString)

	def tourBrakeTime: NodeSeq = Constants.tourBrakeTime / 1000 / 60

	def secsPerTurn: NodeSeq = Constants.secsPerTurn
	def startingMinutes: NodeSeq = Constants.startingMinutes
	def waitingNextTourMinutes: NodeSeq = Constants.secsPerTurn
	def rankLimit: NodeSeq = {
		val r = Constants.rankLimit
		if (r > 0) r.toString else "-"
	}

	def gameTimeout: NodeSeq = {
		val minutes = Constants.gameTimeout.toInt / 1000 / 60
		""+(minutes / 60)+":"+(minutes % 60)
	}

	def conf: NodeSeq = Text(sys.props.getOrElse("run.mode", "none"))

	def timeInMoscow: NodeSeq = Constants.timeLongToHours(System.currentTimeMillis)

	def robocupNumber: NodeSeq = Constants.tournamentName
	def organizatorName: NodeSeq = Constants.organizerName

	def isFourCross: NodeSeq = if (Constants.isFourCross) S ? "yes" else S ? "no"
	def isRated: NodeSeq = if (Constants.isRated) S ? "yes" else S ? "no"

}
