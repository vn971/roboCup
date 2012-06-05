package code
package snippet

import net.liftweb.http._
import js._
import JsCmds._
import comet.ChatServer
import code.comet.ChatMessage
import java.text.SimpleDateFormat
import java.util.TimeZone
import ru.ya.vn91.robotour.Core
import ru.ya.vn91.robotour.StartRegistration
import ru.ya.vn91.robotour.TryRegister
import ru.ya.vn91.robotour.GameFinished
import ru.ya.vn91.robotour.Constants._
import java.util.Date

object Admin {

	def setTime = SHtml.onSubmit(timeAsString => {
		try {
			val startTime = timeStringToLong(timeAsString)
			val regTime = startTime - registrationLength

			Core.core ! StartRegistration(regTime)
			ChatServer ! ChatMessage("Назначен турнир!", 0L, "serv", "")
			ChatServer ! ChatMessage("Начало регистрации: "+timeLongToString(regTime), 0L, "serv", "")
			ChatServer ! ChatMessage("Первый тур (начало игр): "+timeLongToString(startTime), 0L, "serv", "")
			SetValById("timeSetter", "time set.")
		} catch {
			case t: Exception => SetValById("timeSetter", "error. Try again...")
		}
	})

	def startNow = SHtml.onSubmit(nick => {
	})

	def register = SHtml.onSubmit(nick => {
		Core.core ! new TryRegister(nick)
		SetValById("playerRegistrator", "OK, registered:) -Drun.mode="+sys.props.get("run.mode").getOrElse("None"))
	})

	def winGame = SHtml.onSubmit(twoPlayers => {
		val winner = twoPlayers.split("/")(0)
		val looser = twoPlayers.split("/")(1)
		Core.core ! new GameFinished(winner, looser)
		SetValById("winGame", "OK, game result sent:)")
	})
	
	def newTournament = SHtml.onSubmit(s => "")

}