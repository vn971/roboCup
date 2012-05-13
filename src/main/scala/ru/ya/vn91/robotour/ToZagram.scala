package ru.ya.vn91.robotour

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import java.io.InputStreamReader
import scala.io.Source
import java.net.URLEncoder
import ru.ya.vn91.robotour.Utils._

case class AssignGame(val first: String, val second: String, val round: Int = 0)

class ToZagram extends Actor {

	val log = Logging(context.system, this)

	override def preStart() = {
		log.info("inited")
	}

	def receive = {
		case toSend: String => {
			val password = "t7do4MJEsgMQo"
			val idGracza = "kO1v40UcTW"

			// log in
			//			log.info("sending "+toSend)
			val logInURL = "http://zagram.org/auth.py?co=loguj"+
				"&opisGracza="+getServerEncoded("roboTour")+
				"&idGracza="+idGracza+
				"&lang=en"
			getLinkContent(logInURL)

			// send message
			val messageURL = "http://zagram.org/a.kropki"+
				"?idGracza="+idGracza+
				"&co=dodajWpis"+
				"&table=0"+
				"&newMsgs="+getServerEncoded(toSend)+
				"&msgNo=1"
			getLinkContent(messageURL)

			// log out
			val logOutURL = "http://zagram.org/a.kropki"+
				"?playerId="+idGracza+
				"&co=usunGracza"
			getLinkContent(logOutURL)
		}

		case AssignGame(first, second, round) => {
			log.info("assigning game: "+first+"-"+second)
			val url = "http://zagram.org/a.kropki"+
				"?co=setUpTable"+
				"&key="+"j72630brkx6wtp"+
				"&gameType=3030noT4F0.180.5"+
				"&pl1="+getServerEncoded(first)+
				"&pl2="+getServerEncoded(second)+
				"&sayHiTimes=60.60"+
				"&tourn=TEST"+
				"&tRound=round"+round
			// http://zagram.org/a.kropki?co=setUpTable&key=yourKey&gameType=3030noT4r0.180.20&pl1=e&pl2=g&sayHiTimes=60.60&tourn=test&tRound=2%20%28playoff%29
			Utils.getLinkContent(url)
		}
		case any => log.info(any.toString)
	}

}
