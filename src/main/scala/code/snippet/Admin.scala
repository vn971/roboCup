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

object Admin {

	def setTime = SHtml.onSubmit(timeAsString => {
		try {
			val simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.HH:mm")
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"))
			val startTime = simpleDateFormat.parse(timeAsString).getTime()
			Core.core ! StartRegistration(startTime, timeAsString)
			ChatServer ! ChatMessage("Назначен турнир! Начало регистрации: "+timeAsString, 0L, "serv", "")
			SetValById("regTimeSetter", "time set.")
		} catch {
			case t: Exception => SetValById("regTimeSetter", "error. Try again...")
		}
	})

	def startNow = SHtml.onSubmit(nick => {
	})

	def register = SHtml.onSubmit(nick => {
		Core.core ! new TryRegister(nick)
		SetValById("playerRegistrator", "OK, registered:)")
	})

}