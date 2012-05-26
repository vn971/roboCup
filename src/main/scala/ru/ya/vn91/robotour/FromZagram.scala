package ru.ya.vn91.robotour

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import Utils._
import code.comet.ChatServer
import code.comet.ChatMessage
import java.text.SimpleDateFormat
import java.util.TimeZone

class GameInfo(val first: String, val second: String)

class FromZagram extends Actor {

	val log = Logging(context.system, this)
	val gameSet = collection.mutable.HashMap[String, GameInfo]()

	override def preStart() = {
		log.debug("inited")
		var messageCount = 0L
		while (true) {
			val urlAsString = "http://zagram.org/a.kropki"+
				"?idGracza=robot"+
				"&co=getMsg"+
				"&msgNo="+messageCount+
				"&wiad=x"
			val text = getLinkContent(urlAsString)
			log.debug("GET %s Result: %s".format(urlAsString, text))
			for (line <- text.split("/")) {
				val dotSplitted = line.split("\\.")
				if (line.startsWith("ca")) { // || line.startsWith("cr")
					// chat
					try {
						val innerSplit = line.split("\\.", 4)
						val time = innerSplit(0).substring(2).toLong * 1000
						val nick = innerSplit(1)
						// val nickType = dotSplitted(2)
						val chatMessage = getServerDecoded(innerSplit(3))
//						ChatServer ! ChatMessage(chatMessage, time, "zagram", nick)
						if (chatMessage startsWith "!register") {
							context.parent ! new TryRegister(nick)
						}
					} catch {
						case e: NumberFormatException => log.error(e.toString)
					}
				} else if (line.startsWith("m")) {
					try {
						messageCount = line.split("\\.")(1).toLong
					} catch {
						case e: NumberFormatException => log.error(e.toString)
						case e: ArrayIndexOutOfBoundsException => log.error(e.toString)
					}
				} else if (line startsWith "x") {
					val sgfResult = dotSplitted(2)
					val first = gameSet(dotSplitted(0).substring(1)).first
					val second = gameSet(dotSplitted(0).substring(1)).second
					if (sgfResult startsWith "B+") {
						context.parent ! new GameFinished(second, first)
					} else if ((sgfResult startsWith "W+") || sgfResult == "0" || sgfResult == "Void") {
						context.parent ! new GameFinished(first, second)
					} // else still playing
				} else if (line startsWith "d") {
					val tableN = dotSplitted(0).substring(1).toInt
					val first = getServerDecoded(dotSplitted(dotSplitted.size - 1))
					val second = getServerDecoded(dotSplitted(dotSplitted.size - 2))
					gameSet += tableN.toString() -> new GameInfo(first, second)
				}
				// line match {
				// 	case s: String if s.startsWith("ca") => Unit
				// 	case _ => Unit
				// }
			}
			//			context.parent ! new TryRegister("ololo")
			//			context.parent ! ""
			Thread.sleep(5000L)
		}
	}

	def receive = {
		case _ => log.error("zagram reader received something!")
	}

}