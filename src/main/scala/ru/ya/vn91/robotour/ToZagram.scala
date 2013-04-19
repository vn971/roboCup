package ru.ya.vn91.robotour

import akka.actor.Actor
import code.comet.GlobalStatusSingleton
import code.comet.TournamentStatus._
import net.liftweb.common.Loggable
import ru.ya.vn91.robotour.Utils._

case class AssignGame(first: String, second: String, round: Int = 0, sayHiTime: Int = Constants.timeWaitingOpponent)

case class MessageToZagram(message: String)

class ToZagram extends Actor with Loggable {

	override def preStart() {
		logger.info("initialized")
	}

	def receive = {
		case MessageToZagram(toSend) => {
			logger.info(s"sending message $toSend")

			{ // log in
				val logInURL = "http://zagram.org/auth.py?co=loguj"+
					"&opisGracza="+getServerEncoded("RoboCup")+
					"&idGracza="+ToZagram.idGracza+
					"&lang=en"
				getLinkContent(logInURL)
			}

			{ // send message
				val messageURL = "http://zagram.org/a.kropki"+
					"?idGracza="+ToZagram.idGracza+
					"&co=dodajWpis"+
					"&table=0"+
					"&newMsgs="+getServerEncoded(toSend)+
					"&msgNo=1"
				getLinkContent(messageURL)
			}

			{ // log out
				val logOutURL = "http://zagram.org/a.kropki"+
					"?playerId="+ToZagram.idGracza+
					"&co=usunGracza"
				getLinkContent(logOutURL)
			}
		}

		case AssignGame(first, second, round, sayHiTime) => {
			// http://zagram.org/a.kropki?co=setUpTable&key=yourKey&gameType=3030noT4r0.180.20&pl1=e&pl2=g&sayHiTimes=60.60&tourn=test&tRound=2%20%28playoff%29
			logger.info(s"assigning game: $first - $second")
			val url = "http://zagram.org/a.kropki"+
				"?co=setUpTable"+
				"&key="+ToZagram.assignGamePassword+
				"&gameType="+Constants.zagramGameSettings+
				"&pl1="+getServerEncoded(first)+
				"&pl2="+getServerEncoded(second)+
				"&sayHiTimes="+sayHiTime+"."+sayHiTime+
				"&tourn="+getServerEncoded(Constants.tournamentName)+
				"&tRound=0"
			val reply = Utils.getLinkContent(url)
			logger.info(s"url: $url")
			logger.info(s"reply: $reply")
		}
	}

}

object ToZagram extends {
	val idGracza = sys.props.get("zagram.idGracza").getOrElse {
		GlobalStatusSingleton ! ErrorStatus("zagram idGracza not found!")
		""
	}
	val assignGamePassword = sys.props.get("zagram.assignGamePassword").getOrElse {
		GlobalStatusSingleton ! ErrorStatus("zagram gameAssignPass not found!")
		""
	}
}