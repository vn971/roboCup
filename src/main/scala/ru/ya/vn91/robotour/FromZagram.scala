package ru.ya.vn91.robotour

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import Utils._
import code.comet.ChatServer
import java.text.SimpleDateFormat
import java.util.TimeZone

case class GameInfo(val first: String, val second: String)
case class PlayerInfo(val nick: String, val language: String, val rank: Int, val wins: Int, val losses: Int, val draws: Int)

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
				val dotSplitted = line.substring(1).split("\\.")
				if (line.startsWith("ca")) { // chat
					try {
						val innerSplit = line.split("\\.", 4)
						val time = innerSplit(0).substring(2).toLong * 1000
						val nick = innerSplit(1)
						val chatMessage = getServerDecoded(innerSplit(3))
						//						ChatServer ! ChatMessage(chatMessage, time, "zagram", nick)
						if (chatMessage.startsWith("!register")) {
							playerSet.get(nick) match {
								case None =>
									log.info("tried to register, but error occured: "+nick)
									context.parent ! MessageToZagram("Sorry, cound not find zagram rank for "+nick)
								case Some(info) =>
									log.info("tried to register: "+nick)
									context.parent ! TryRegister(info)
							}
						}
					} catch {
						case e: Exception => log.error(e.toString)
					}
				} else if (line.startsWith("m")) { // message count
					try {
						messageCount = line.split("\\.")(1).toLong
					} catch {
						case e: NumberFormatException => log.error(e.toString)
						case e: ArrayIndexOutOfBoundsException => log.error(e.toString)
					}
				} else if (line startsWith "x") { // game state info
					val sgfResult = dotSplitted(2)
					val first = gameSet(dotSplitted(0)).first
					val second = gameSet(dotSplitted(0)).second
					if (sgfResult startsWith "B+") {
						context.parent ! GameWon(first, second)
					} else if (sgfResult startsWith "W+") {
						context.parent ! GameWon(second, first)
					} else if (sgfResult == "0" || sgfResult == "Void") {
						context.parent ! GameDraw(first, second)
					} // else still playing
				} else if (line startsWith "d") {
					val tableN = dotSplitted(0).toInt
					val first = getServerDecoded(dotSplitted(dotSplitted.size - 2))
					val second = getServerDecoded(dotSplitted(dotSplitted.size - 1))
					gameSet += tableN.toString() -> GameInfo(first, second)
				} else if (line startsWith "i") { // player info
					val player = dotSplitted(0)
					val language = dotSplitted(3)
					val (rating, wins, losses, draws) =
						if (dotSplitted.size == 4)
							(1100, 0, 0, 0)
						else
							(dotSplitted(4).toInt, dotSplitted(5).toInt, dotSplitted(7).toInt, dotSplitted(6).toInt)
					playerSet += player -> PlayerInfo(player, language, rating, wins, losses, draws)
				}
			}
			Thread.sleep(5000L)
		}
	}

	def receive = {
		case _ => log.error("zagram reader received something!")
	}

}