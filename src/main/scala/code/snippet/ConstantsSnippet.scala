package code.snippet

import net.liftweb.common.Loggable
import net.liftweb.http.S
import net.liftweb.util.ClearNodes
import net.liftweb.util.Helpers._
import ru.ya.vn91.robotour.Constants


object ConstantsSnippet extends Loggable {

	def tourBrakeTime = ".value *" #> Constants.breakTime.toMinutes
	def secsPerTurn = ".value *" #> Constants.perTurnTime.toSeconds
	def startingMinutes = ".value *" #> Constants.startingTime.toMinutes
	def organizerName = ".value *" #> (S ? Constants.organizerCodename)

	def isFourCross = ".value *" #> (if (Constants.isFourCross) S ? "yes" else S ? "no")
	def isRated = ".value *" #> (if (Constants.isRated) S ? "yes" else S ? "no")

	def rankLimit = Constants.rankLimit.map(".value *" #> _).openOr(ClearNodes)


	def gameTimeout = "*" #> {
		val minutes = Constants.gameTimeout.toMinutes
		(minutes / 60) + ":" + (minutes % 60)
	}

	def conf = "*" #> sys.props.getOrElse("run.mode", "none")

	def timeInMoscow = "*" #> Constants.timeLongToHours(System.currentTimeMillis)

	def expectedGameTime = "* *" #> Constants.expectedGameTime.toMinutes
	def expectedTourTime = "* *" #> Constants.expectedTourTime.toMinutes

}
