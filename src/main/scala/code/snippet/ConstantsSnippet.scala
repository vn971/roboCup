package code.snippet

import net.liftweb.http.S
import ru.ya.vn91.robotour.Constants
import scala.language.implicitConversions
import scala.xml._


object ConstantsSnippet {

	implicit def toNodeSeq(i: Int) = Text(i.toString)
	implicit def toNodeSeq(l: Long) = Text(l.toString)

	def tourBrakeTime: NodeSeq = Constants.tourBrakeTime / 1000 / 60

	def secsPerTurn: NodeSeq = Constants.secsPerTurn
	def startingMinutes: NodeSeq = Constants.startingMinutes
	def waitingNextTourMinutes: NodeSeq = Constants.secsPerTurn
	def rankLimit: NodeSeq = {
		val r = Constants.rankLimit
		Text(if (r > 0) r.toString else "-")
	}

	def gameTimeout: NodeSeq = {
		val minutes = Constants.gameTimeout.toInt / 1000 / 60
		Text(""+(minutes / 60)+":"+(minutes % 60))
	}

	def conf: NodeSeq = Text(sys.props.getOrElse("run.mode", "none"))

	def timeInMoscow = Text(Constants.timeLongToHours(System.currentTimeMillis))

	def robocupNumber = Text(Constants.tournamentName)
	def organizatorName = Text(S ? Constants.organizerName)

	def isFourCross = Text(if (Constants.isFourCross) S ? "yes" else S ? "no")
	def isRated = Text(if (Constants.isRated) S ? "yes" else S ? "no")

}
