package ru.ya.vn91.robotour

import akka.actor.Actor
import net.liftweb.common.Loggable
import ru.ya.vn91.robotour.Utils._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

case class GameInfo(first: String, second: String)

case class PlayerInfo(nick: String, language: String, rank: Int, wins: Int, losses: Int, draws: Int)

class FromZagram extends Actor with Loggable {

	private case object Tick

	val gameSet = collection.mutable.HashMap[String, GameInfo]()
	val playerSet = collection.mutable.HashMap[String, PlayerInfo]()
	var messageCount = 0L

	override def preStart() {
		logger.debug("initialized")
		self ! Tick
	}

	def receive = {
		case Tick => try {
			val urlAsString = "http://zagram.org/a.kropki" +
					"?idGracza=robot" +
					"&co=getMsg" +
					"&msgNo=" + messageCount +
					"&wiad=x"
			val text = getLinkContent(urlAsString)
			logger.trace(s"GET $urlAsString Result: $text")
			for (line <- text.split('/').filter(_.length > 0)) {
				val dotSplitted = line.substring(1).split('.')
				if (line.startsWith("ca")) {
					// chat
					try {
						val innerSplit = line.split("\\.", 4)
//						val time = innerSplit(0).substring(2).toLong * 1000
						val nick = innerSplit(1)
						val chatMessage = getServerDecoded(innerSplit(3))
						//						ChatServer ! ChatMessage(chatMessage, time, "zagram", nick)
						if (
							chatMessage.startsWith("!register") ||
									chatMessage.startsWith("!register") ||
									chatMessage.startsWith("!register")) {
							playerSet.get(nick) match {
								case None =>
									logger.info("tried to register, but error occurred: " + nick)
									context.parent ! MessageToZagram("Sorry, could not find zagram rank for " + nick)
								case Some(info) =>
									logger.info("tried to register: " + nick)
									context.parent ! TryRegister(info)
							}
						}
					} catch {
						case e: Exception => logger.error(e.toString)
					}
				} else if (line.startsWith("m")) {
					// message count
					try {
						messageCount = line.split('.')(1).toLong
					} catch {
						case e: NumberFormatException => logger.error(e.toString)
						case e: ArrayIndexOutOfBoundsException => logger.error(e.toString)
					}
				} else if (line startsWith "x") {
					// game state info
					val sgfResult = dotSplitted(2)
					val first = gameSet(dotSplitted(0)).first
					val second = gameSet(dotSplitted(0)).second
					if (sgfResult startsWith "B+") {
						context.parent ! GameWon(first, second)
					} else if ((sgfResult startsWith "W+") || (sgfResult == "Void")) {
						context.parent ! GameWon(second, first)
					} else if (sgfResult == "0") {
						context.parent ! GameDraw(first, second)
					} // else still playing
				} else if (line startsWith "d") {
					val tableN = dotSplitted(0).toInt
					val first = getServerDecoded(dotSplitted(dotSplitted.length - 2))
					val second = getServerDecoded(dotSplitted(dotSplitted.length - 1))
					gameSet += tableN.toString -> GameInfo(first, second)
				} else if (line startsWith "i") {
					// player info
					val player = dotSplitted(0)
					val language = dotSplitted(3)
					val (rating, wins, losses, draws) =
						if (dotSplitted.length == 4)
							(1100, 0, 0, 0)
						else
							(dotSplitted(4).toInt, dotSplitted(5).toInt, dotSplitted(7).toInt, dotSplitted(6).toInt)
					playerSet += player -> PlayerInfo(player, language, rating, wins, losses, draws)
				}
			}
		} catch {
			case e: Exception => logger.error(e.toString)
		}
		context.system.scheduler.scheduleOnce(5.seconds, self, Tick)
	}

}
