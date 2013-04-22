package code.snippet

import net.liftweb.http.S
import net.liftweb.util.ClearNodes
import net.liftweb.util.Helpers._
import ru.ya.vn91.robotour.Constants
import scala.xml._


object ConstantsSnippet {

	def tourBrakeTime = ".value *" #> (Constants.tourBrakeTime / 1000 / 60).toString

	def secsPerTurn = ".value *" #> Constants.secondsPerTurn
	def startingMinutes = ".value *" #> Constants.startingMinutes

	def rankLimit = Constants.rankLimit.map(".value *" #> _).getOrElse(ClearNodes)

	def gameTimeout = "*" #> {
		val minutes = Constants.gameTimeout.toInt / 1000 / 60
		(minutes / 60) + ":" + (minutes % 60)
	}

	def conf = "*" #> sys.props.getOrElse("run.mode", "none")

	def timeInMoscow = Text(Constants.timeLongToHours(System.currentTimeMillis))

	def organizerName = ".value *" #> (S ? Constants.organizerCodename)

	def isFourCross = ".value *" #> (if (Constants.isFourCross) S ? "yes" else S ? "no")
	def isRated = ".value *" #> (if (Constants.isRated) S ? "yes" else S ? "no")

}
