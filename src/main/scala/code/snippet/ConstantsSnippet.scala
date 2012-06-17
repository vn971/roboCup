package code.snippet

import net.liftweb.util._
import Helpers._
import ru.ya.vn91.robotour.Constants

object ConstantsSnippet {

	def tourBrakeTime = "*" #> (Constants.tourBrakeTime / 1000 / 60)

	def secsPerTurn = "*" #> Constants.secsPerTurn.toString

	def gameTimeout = "*" #> {
		val minutes = Constants.gameTimeout.toInt / 1000 / 60
		""+(minutes / 60)+":"+(minutes % 60)
	}

	def conf = "*" #> sys.props.get("run.mode").getOrElse("none")

	def timeInMoscow = "*" #> Constants.timeLongToHours(System.currentTimeMillis)

	def robocupNumber = "*" #> Constants.tournamentName
	
}