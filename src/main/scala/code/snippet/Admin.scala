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
import ru.ya.vn91.robotour.GameWon
import ru.ya.vn91.robotour.Constants._
import java.util.Date
import net.liftweb.common.Logger
import code.comet.MessageFromAdmin
import akka.actor.Props
import ru.ya.vn91.robotour.ToZagram
import ru.ya.vn91.robotour.AssignGame
import ru.ya.vn91.robotour.PlayerInfo
import code.comet.GlobalStatusSingleton
import code.comet.status.CustomStatus

object Admin {

	private val log = Logger(Admin.getClass)

	def setTime = SHtml.onSubmit(timeAsString => {
		try {
			log.info("tournament time set ("+timeAsString+")")
			val startTime = timeStringToLong(timeAsString)
			val regTime = startTime - registrationLength
			Core.core ! StartRegistration(regTime)
			SetValById("timeSetter", "time set.")
		} catch {
			case t: Exception => SetValById("timeSetter", "error. Try again...")
		}
	})

	def startNow = SHtml.onSubmit(nick => {
	})

	def register = SHtml.onSubmit(nick => {
		log info "registered "+nick+"."
		Core.core ! new TryRegister(PlayerInfo(nick,"en",1200,0,0,0))
		SetValById("playerRegistrator", "")
	})

	def setStatus = SHtml.onSubmit(status => {
		log info "setting status "+status
		GlobalStatusSingleton ! CustomStatus(status)
		SetValById("setStatus", "")
	})

	def winGame = SHtml.onSubmit(twoPlayers => {
		log info "assigning winner: "+twoPlayers
		val winner = twoPlayers.split("/")(0)
		val looser = twoPlayers.split("/")(1)
		Core.core ! GameWon(winner, looser)
		SetValById("winGame", "OK, game result sent")
	})

	def assignGame = SHtml.onSubmit(twoPlayers => {
		log info "assigning game: "+twoPlayers
		val first = twoPlayers.split("/")(0)
		val second = twoPlayers.split("/")(1)
		Core.system.actorOf(Props[ToZagram], name = "core.toZagram") ! AssignGame(first, second)
		SetValById("assignGame", "")
	})

	def newTournament = SHtml.onSubmit(s => "")

}