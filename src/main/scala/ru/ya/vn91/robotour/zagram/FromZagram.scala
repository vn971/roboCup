package ru.ya.vn91.robotour.zagram

import akka.actor.Actor
import net.liftweb.common.Loggable
import ru.ya.vn91.robotour.Utils._
import ru.ya.vn91.robotour._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

class FromZagram extends Actor with Loggable {

	private case object Tick

	private val gameSet = collection.mutable.HashMap[String, GameInfo]()
	private val playerSet = collection.mutable.HashMap[String, PlayerInfo]()
	private var messageCount = 0L
	private val idGracza = Random.nextInt(999999).toString

	override def preStart() {
		logger.debug("initialized")
		self ! Tick
	}

	def receive = {
		case Tick => try {
			val urlAsString = s"http://zagram.org/a.kropki?idGracza=$idGracza&co=getMsg&msgNo=$messageCount&wiad=x"
			val text = getLinkContent(urlAsString)
			logger.trace(s"HTTP GET $urlAsString => $text")
			if (text.startsWith("sd") && text.endsWith("end")) {
				for (line <- text.split('/').filter(_.length > 0)) try {
					val dotSplit = line.substring(1).split('.')
					if (line.startsWith("ca")) {
						// chat
						val innerSplit = line.split("\\.", 4)
						val nick = innerSplit(1)
						val chatMessage = getServerDecoded(innerSplit(3))
						// ChatServer ! ChatMessage(chatMessage, time, "zagram", nick)
						if (
							chatMessage.startsWith("!register") ||
									chatMessage.startsWith("!register") ||
									chatMessage.startsWith("!register")) {
							playerSet.get(nick) match {
								case None =>
									logger.info(s"failed to register: no userinfo for $nick")
									context.parent ! MessageToZagram(s"Sorry, could not find zagram rank for $nick")
								case Some(info) =>
									logger.info(s"tried to register: $nick")
									context.parent ! TryRegister(info)
							}
						}
					} else if (line.startsWith("m")) {
						// message count
						messageCount = line.split('.')(1).toLong
					} else if (line startsWith "x") {
						// game state info
						val sgfResult = dotSplit(2)
						val first = gameSet(dotSplit(0)).first
						val second = gameSet(dotSplit(0)).second
						if (sgfResult startsWith "B+") {
							context.parent ! GameWon(first, second)
						} else if ((sgfResult startsWith "W+") || (sgfResult == "Void")) {
							context.parent ! GameWon(second, first)
						} else if (sgfResult == "0") {
							context.parent ! GameDraw(first, second)
						} // else still playing
					} else if (line startsWith "d") {
						val tableN = dotSplit(0).toInt
						val first = getServerDecoded(dotSplit(dotSplit.length - 2))
						val second = getServerDecoded(dotSplit(dotSplit.length - 1))
						gameSet += tableN.toString -> GameInfo(first, second)
					} else if (line startsWith "i") {
						// player info
						val player = dotSplit(0)
						val (rating, wins, losses, draws) =
							if (line startsWith "i*")
								(0, 0, 0, 0)
							else
								(dotSplit(4).toInt, dotSplit(5).toInt, dotSplit(7).toInt, dotSplit(6).toInt)
						playerSet += player -> PlayerInfo(player, rating, wins, losses, draws)
					}
				} catch {
					case e: Exception =>
						logger.warn(s"error processing line: $line, exception: $e")
				}
			} else {
				logger.info("incorrect server response")
			}
		} catch {
			case e: Exception =>
				logger.warn(s"error in the main loop: $e")
		}
		context.system.scheduler.scheduleOnce(7.seconds, self, Tick)
	}

}

case class GameInfo(first: String, second: String)

case class PlayerInfo(nick: String, rank: Int, wins: Int, losses: Int, draws: Int)
