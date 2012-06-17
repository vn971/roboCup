package code
package snippet

import net.liftweb.http._
import js._
import JsCmds._
import comet.ChatServer
import java.text.SimpleDateFormat
import java.util.TimeZone
import ru.ya.vn91.robotour.Core
import ru.ya.vn91.robotour.StartRegistration
import ru.ya.vn91.robotour.TryRegister
import ru.ya.vn91.robotour.GameFinished
import ru.ya.vn91.robotour.Constants._
import java.util.Date
import net.liftweb.common.Logger
import code.comet.MessageFromAdmin

object Admin {

	private val log = Logger(Admin.getClass)

	def setTime = SHtml.onSubmit(timeAsString => {
		try {
			log.info("tournament time set ("+timeAsString+")")
			val startTime = timeStringToLong(timeAsString)
			val regTime = startTime - registrationLength

			Core.core ! StartRegistration(regTime)
			ChatServer ! MessageFromAdmin("Назначен турнир!")
			ChatServer ! MessageFromAdmin("Начало регистрации: "+timeLongToString(regTime))
			ChatServer ! MessageFromAdmin("Первый тур (начало игр): "+timeLongToString(startTime))
			SetValById("timeSetter", "time set.")
		} catch {
			case t: Exception => SetValById("timeSetter", "error. Try again...")
		}
	})

	def startNow = SHtml.onSubmit(nick => {
	})

	def register = SHtml.onSubmit(nick => {
		log info "registered "+nick+"."
		Core.core ! new TryRegister(nick)
		SetValById("playerRegistrator", "OK, registered:) -Drun.mode="+sys.props.get("run.mode").getOrElse("None"))
	})

	def winGame = SHtml.onSubmit(twoPlayers => {
		log info "game winner assigned: "+twoPlayers
		val winner = twoPlayers.split("/")(0)
		val looser = twoPlayers.split("/")(1)
		Core.core ! new GameFinished(winner, looser)
		SetValById("winGame", "OK, game result sent:)")
	})

	def newTournament = SHtml.onSubmit(s => "")

}