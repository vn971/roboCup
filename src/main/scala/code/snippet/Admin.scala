package code.snippet

import akka.actor.Props
import code.comet.GlobalStatusSingleton
import code.comet.TournamentStatus._
import net.liftweb.common.Loggable
import net.liftweb.http._
import net.liftweb.http.js.JsCmds._
import ru.ya.vn91.robotour.Constants._
import ru.ya.vn91.robotour._
import ru.ya.vn91.robotour.zagram.{AssignGame, ToZagram, PlayerInfo}

object Admin extends Loggable {

	def setTime() = SHtml.onSubmit(timeAsString => {
		try {
			logger.info("tournament time set (" + timeAsString + ")")
			val startTime = timeStringToLong(timeAsString)
			val regTime = startTime - registrationTime.toMillis
			Core.core ! StartRegistration(regTime)
			SetValById("timeSetter", "time set.")
		} catch {
			case t: Exception => SetValById("timeSetter", "error. Try again...")
		}
	})

	def register = SHtml.onSubmit(nick => {
		logger.info(s"registered $nick")
		Core.core ! new TryRegister(PlayerInfo(nick, 1200, 0, 0, 0))
		SetValById("playerRegistrator", "")
	})

	def setStatus() = SHtml.onSubmit(status => {
		logger.info(s"setting status $status")
		GlobalStatusSingleton ! CustomStatus(status)
		SetValById("setStatus", "")
	})

	def winGame = SHtml.onSubmit { twoPlayers =>
		(for {
			winner <- twoPlayers.split('/').lift(0)
			looser <- twoPlayers.split('/').lift(1)
		} yield {
			logger.info(s"assigning game result: $winner > $looser")
			Core.core ! GameWon(winner, looser)
			SetValById("winGame", "OK, game result sent")
		}).getOrElse {
			Alert("ERROR")
		}
	}

	def assignGame = SHtml.onSubmit(twoPlayers => {
		(for {
			first <- twoPlayers.split('/').lift(0)
			second <- twoPlayers.split('/').lift(1)
		} yield {
			logger.info(s"assigning game: $twoPlayers")
			Core.system.actorOf(Props[ToZagram], name = "core.toZagram") ! AssignGame(first, second)
			SetValById("assignGame", "OK, assigned")
		}).getOrElse {
			Alert("ERROR")
		}
	})

	def newTournament = SHtml.onSubmit(s => "")

}
