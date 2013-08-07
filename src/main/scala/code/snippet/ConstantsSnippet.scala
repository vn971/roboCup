package code.snippet

import net.liftweb.common.Loggable
import net.liftweb.http.S
import net.liftweb.util.Helpers._
import ru.ya.vn91.robotour.Constants
import ru.ya.vn91.robotour.Constants._
import scala.xml.Text


object ConstantsSnippet extends Loggable {

	def gameSettings =
		".rankLimit" #> rankLimit.map(".value *" #> _) &
				".tourBrakeTime *" #> breakTime.toMinutes &
				".secsPerTurn *" #> perTurnTime.toSeconds &
				".fieldSize *" #> Text(s"$fieldSizeX x $fieldSizeY") &
				".startingMinutes *" #> startingTime.toMinutes &
				".crossesCount *" #> crossesCount &
				".scoringTerritory *" #> S.?(s"scoringTerritory=$withTerritory") &
				".isRated *" #> (if (isRated) S ? "yes" else S ? "no") &
				".expectedGameTime *" #> expectedGameTime.toMinutes &
				".expectedTourTime *" #> expectedTourTime.toMinutes &
				".organizerName *" #> (S ? organizerCodename) &
				".gameTimeout *" #> s"${gameTimeout.toMinutes / 60}:${gameTimeout.toMinutes % 60}"

	def conf = Text(sys.props.getOrElse("run.mode", "none"))

	def timeInMoscow = Text(Constants.timeLongToHours(System.currentTimeMillis))

}
