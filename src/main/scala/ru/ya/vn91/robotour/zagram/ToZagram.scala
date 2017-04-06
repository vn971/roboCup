package ru.ya.vn91.robotour.zagram

import akka.actor.Actor
import net.liftweb.common.Loggable
import ru.ya.vn91.robotour.Constants
import ru.ya.vn91.robotour.Utils._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


case class MessageToZagram(message: String)

class ToZagram extends Actor with Loggable {

	def receive: Receive = {
		case MessageToZagram(toSend) =>
			for (idGracza <- Constants.zagramIdGracza) {
				logger.info(s"sending message $toSend")

				val logIn = dispatch.url("http://zagram.org/auth.py").
					addQueryParameter("co", "loguj").
					addQueryParameter("opisGracza", "RoboCup").
					addQueryParameter("idGracza", idGracza).
					addQueryParameter("lang", "en").url

				val sendMessage = dispatch.url("http://zagram.org/a.kropki").
					addQueryParameter("idGracza", idGracza).
					addQueryParameter("co", "dodajWpis").
					addQueryParameter("table", "0").
					addQueryParameter("newMsgs", toSend).
					addQueryParameter("msgNo", "1").url

				val logOut = dispatch.url("http://zagram.org/a.kropki").
					addQueryParameter("playerId", idGracza).
					addQueryParameter("co", "usunGracza").url

				getLinkContent(logIn).suppressWartRemover()
				getLinkContent(sendMessage).suppressWartRemover()
				getLinkContent(logOut).suppressWartRemover()
			}
	}

}

object ToZagram extends Loggable {

	def assignGame(first: String, second: String, round: Int = 0, isInfiniteTime: Boolean = false): Unit = {
		for (password <- Constants.zagramAssignGamePassword) {
			// http://zagram.org/a.kropki?co=setUpTable&key=yourKey&gameType=3030noT4r0.180.20&pl1=e&pl2=g&sayHiTimes=60.60&tourn=test&tRound=2%20%28playoff%29
			logger.info(s"assigning game: $first - $second")
			val sayHiTime = if (isInfiniteTime) 0L else Constants.sayHiTime.toSeconds
			val rated = {
				val ratingFirst = ZagramInMemoryData.playerSet.get(first).map(_.rank)
				val ratingSecond = ZagramInMemoryData.playerSet.get(second).map(_.rank)
				for {
					f <- ratingFirst
					s <- ratingSecond
				} yield math.abs(f - s) <= Constants.gameRatedIfRankDiff
			}.getOrElse(true)

			val request = dispatch.url("http://zagram.org/a.kropki").
				addQueryParameter("co", "setUpTable").
				addQueryParameter("key", password).
				addQueryParameter("gameType", Constants.zagramGameSettings(isInfiniteTime, rated)).
				addQueryParameter("pl1", first).
				addQueryParameter("pl2", second).
				addQueryParameter("sayHiTimes", sayHiTime + "." + sayHiTime).
				addQueryParameter("tourn", Constants.zagramTournamentCodename).
				addQueryParameter("tRound", round.toString)

			dispatch.Http(request.OK(dispatch.as.String)).onComplete {
				case Success(s: String) =>
					logger.info(s"zagram game assigned: ${request.url} => $s")
				case Failure(s: Throwable) =>
					logger.error(s"zagram game assign failure, url: ${request.url} exception: $s")
			}
		}
	}

}
