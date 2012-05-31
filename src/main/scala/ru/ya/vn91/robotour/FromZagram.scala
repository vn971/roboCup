package ru.ya.vn91.robotour

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import Utils._
import code.comet.ChatServer
import code.comet.ChatMessage
import java.text.SimpleDateFormat
import java.util.TimeZone

case class GameInfo(val first: String, val second: String)
case class PlayerInfo(val nick: String, val wins: Int, val losses: Int, val draws: Int)

class FromZagram extends Actor {

	val log = Logging(context.system, this)
	val gameSet = collection.mutable.HashMap[String, GameInfo]()
	val playerSet = collection.mutable.HashMap[String, PlayerInfo]()

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
						if (chatMessage.startsWith("!register")) {
							val info = playerSet(nick)
							if (info == null) {
							}
							context.parent ! TryRegister(nick)
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
						context.parent ! GameFinished(second, first)
					} else if (sgfResult startsWith "W+") {
						context.parent ! GameFinished(first, second)
					} else if (sgfResult == "0" || sgfResult == "Void") {
						// the player who moves second is "first"...
						// so, if you want the second player to win, you should write 
						// "GameFinished(first, second)"
						if (util.Random.nextBoolean) {
							context.parent ! GameFinished(first, second)
						} else {
							context.parent ! GameFinished(second, first)
						}
					} // else still playing
				} else if (line startsWith "d") {
					val tableN = dotSplitted(0).substring(1).toInt
					val first = getServerDecoded(dotSplitted(dotSplitted.size - 1))
					val second = getServerDecoded(dotSplitted(dotSplitted.size - 2))
					gameSet += tableN.toString() -> GameInfo(first, second)
				}
			}
			Thread.sleep(5000L)
		}
	}

	def receive = {
		case _ => log.error("zagram reader received something!")
	}

}