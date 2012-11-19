package ru.ya.vn91.robotour

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import java.net.URLEncoder
import ru.ya.vn91.robotour.Utils._
import Constants._
import code.comet.GlobalStatusSingleton
import code.comet.status.ErrorStatus

case class AssignGame(val first: String, val second: String, val round: Int = 0, val sayHiTime: Int = timeWaitingOpponent)

case class MessageToZagram(val message: String)

class ToZagram extends Actor {

	val log = Logging(context.system, this)

	override def preStart() = {
		log.info("inited")
	}

	def receive = {
		case MessageToZagram(toSend) => {
			log.info("sending message "+toSend)

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
			log.info("assigning game: "+first+"-"+second)
			val url = "http://zagram.org/a.kropki"+
				"?co=setUpTable"+
				"&key="+ToZagram.assignGamePassword+
				"&gameType="+zagramGameSettings+
				"&pl1="+getServerEncoded(first)+
				"&pl2="+getServerEncoded(second)+
				"&sayHiTimes="+sayHiTime+"."+sayHiTime+
				"&tourn="+getServerEncoded(tournamentName)+
				"&tRound=0"
			val reply = Utils.getLinkContent(url)
			log.info("url: "+url)
			log.info("reply: "+reply)
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