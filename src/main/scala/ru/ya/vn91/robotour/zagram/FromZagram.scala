package ru.ya.vn91.robotour.zagram

import akka.actor.Actor
import net.liftweb.common.Loggable
import ru.ya.vn91.robotour.Utils._
import ru.ya.vn91.robotour._
import scala.collection.immutable.HashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

case class GameInfo(first: String, second: String)

case class PlayerInfo(nick: String, rank: Int, wins: Int, losses: Int, draws: Int)

class FromZagram extends Actor with Loggable {

	private case object Tick

	private var gameSet = HashMap[String, GameInfo]()
	private var playerSet = HashMap[String, PlayerInfo]()
	private var messageCount = 0L
	private val idGracza = Random.nextInt(999999).toString

	override def preStart(): Unit = {
		logger.debug("initialized")
		self ! Tick
	}

	def receive = {
		case Tick => try { // not ideologically right, but needed for zagram
			context.system.scheduler.scheduleOnce(7.seconds, self, Tick).suppressWartRemover()
			val urlAsString = dispatch.url("http://zagram.org/a.kropki").
				addQueryParameter("idGracza", idGracza).
				addQueryParameter("co", "getMsg").
				addQueryParameter("msgNo", messageCount.toString).
				addQueryParameter("wiad", "x").url

			// async impossible because of zagram protocol
			val tryZagramResponse = getLinkContent(urlAsString)

			logger.trace(s"assign game: $urlAsString => $tryZagramResponse")
			for {
				response <- tryZagramResponse.toOption
				if response.startsWith("sd") && response.endsWith("end")
				line <- response.split('/')
			} {
				handleLine(line)
			}
		} catch {
			case e: Exception => logger.error("exception in main cycle", e)
		}

	}

	private def handleLine(line: String) = try {
		val dotSplit = line.drop(1).split('.')
		if (line startsWith "m") {
			val newMessageCount = line.split('.')(1).toLong
			if (newMessageCount < messageCount) {
				logger.warn(s"message counter decreased, seems like a server restart ($messageCount => $newMessageCount)")
			}
			messageCount = newMessageCount
		} else if (line startsWith "ca") {
			handleChat(dotSplit, line)
		} else if (line startsWith "x") {
			handleGameState(dotSplit)
		} else if (line startsWith "d") {
			handleGameDescription(dotSplit)
		} else if (line startsWith "i") {
			handleUserInfo(dotSplit)
		}
	} catch {
		case e: Exception => logger.warn(s"error processing line: $line", e)
	}

	private def handleChat(dotSplit: Array[String], line: String): Unit = {
		val innerSplit = line.split("\\.", 4)
		val nick = innerSplit(1)
		val msg = getZagramDecoded(innerSplit(3)).toLowerCase
		if (msg.startsWith("!register") ||
			msg.startsWith("!register") ||
			msg.startsWith("!register")) {
			playerSet.get(nick) match {
				case _ if nick.startsWith("*") =>
					logger.info(s"registration attempt failed, guests not allowed: $nick")
					context.parent ! MessageToZagram(s"$nick, to take a part in the tournament, please, use a registered account.")
				case Some(info) =>
					logger.info(s"registration attempt caught: $info")
					context.parent ! TryRegister(info)
				case None =>
					logger.info(s"registration attempt failed: no user info for $nick")
					context.parent ! MessageToZagram(s"Sorry, could not find zagram rank for $nick")
			}
		}
	}

	private def handleGameState(dotSplit: Array[String]): Unit = {
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
	}

	private def handleGameDescription(dotSplit: Array[String]): Unit = {
		val tableN = dotSplit(0).toInt
		val first = getZagramDecoded(dotSplit(dotSplit.length - 2))
		val second = getZagramDecoded(dotSplit(dotSplit.length - 1))
		gameSet += tableN.toString -> GameInfo(first, second)
	}

	private def handleUserInfo(dotSplit: Array[String]): Unit = {
		val player = dotSplit(0)
		val (rating, wins, losses, draws) =
			if (player startsWith "*")
				(0, 0, 0, 0)
			else
				(dotSplit(4).toInt, dotSplit(5).toInt, dotSplit(7).toInt, dotSplit(6).toInt)
		playerSet += player -> PlayerInfo(player, rating, wins, losses, draws)
	}

}
