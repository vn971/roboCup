package ru.ya.vn91.robotour.zagram

import akka.actor.Actor
import net.liftweb.common.Loggable
import ru.ya.vn91.robotour.Utils._
import ru.ya.vn91.robotour.{Utils, Constants}

case class AssignGame(
		first: String, second: String,
		round: Int = 0,
		infiniteTime: Boolean = false)

case class MessageToZagram(message: String)

class ToZagram extends Actor with Loggable {

	override def preStart() {
		logger.info("initialized")
	}

	def receive = {
		case MessageToZagram(toSend) => {
			for (idGracza <- Constants.zagramIdGracza) {
				logger.info(s"sending message $toSend")

				{
					val logInURL = "http://zagram.org/auth.py?co=loguj" +
							"&opisGracza=" + getServerEncoded("RoboCup") +
							"&idGracza=" + idGracza +
							"&lang=en"
					getLinkContent(logInURL)
				}

				{
					val sendMessageURL = "http://zagram.org/a.kropki" +
							"?idGracza=" + idGracza +
							"&co=dodajWpis" +
							"&table=0" +
							"&newMsgs=" + getServerEncoded(toSend) +
							"&msgNo=1"
					getLinkContent(sendMessageURL)
				}

				{
					val logOutURL = s"http://zagram.org/a.kropki?playerId=$idGracza&co=usunGracza"
					getLinkContent(logOutURL)
				}
			}
		}

		case AssignGame(first, second, round, isInfiniteTime) => {
			for (password <- Constants.zagramAssignGamePassword) {
				// http://zagram.org/a.kropki?co=setUpTable&key=yourKey&gameType=3030noT4r0.180.20&pl1=e&pl2=g&sayHiTimes=60.60&tourn=test&tRound=2%20%28playoff%29
				logger.info(s"assigning game: $first - $second")
				val sayHiTime: Long = if (isInfiniteTime) 0 else Constants.sayHiTime.toSeconds

				val url = "http://zagram.org/a.kropki" +
						"?co=setUpTable" +
						"&key=" + password +
						"&gameType=" + Constants.zagramGameSettings(isInfiniteTime) +
						"&pl1=" + getServerEncoded(first) +
						"&pl2=" + getServerEncoded(second) +
						"&sayHiTimes=" + sayHiTime + "." + sayHiTime +
						"&tourn=" + getServerEncoded(Constants.tournamentCodename) +
						"&tRound=" + round
				val reply = Utils.getLinkContent(url)
				logger.info(s"HTTP GET (assign game): $url => $reply")
			}
		}
	}

}
