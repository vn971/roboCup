package ru.ya.vn91.robotour.zagram

import akka.actor.Actor
import net.liftweb.common.Loggable
import ru.ya.vn91.robotour.Utils._
import ru.ya.vn91.robotour._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random


case class GameInfo(first: String, second: String)

case class PlayerInfo(nick: String, rank: Int, wins: Int, losses: Int, draws: Int)

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
		case Tick =>
			try {
				val urlAsString = s"http://zagram.org/a.kropki?idGracza=$idGracza&co=getMsg&msgNo=$messageCount&wiad=x"
				val zagramResponseBox = getLinkContent(urlAsString)
				logger.trace(s"HTTP GET $urlAsString => $zagramResponseBox")
				for {
					response <- zagramResponseBox
					if response.startsWith("sd") && response.endsWith("end")
					line <- response.split('/') if line.length > 0
				} {
					handleLine(line)
				}
			} catch {
				case e: Exception => logger.warn(s"error in the main loop: $e")
			}
			context.system.scheduler.scheduleOnce(7.seconds, self, Tick)
	}

	private def handleLine(line: String) = try {
		val dotSplit = line.substring(1).split('.')
		if (line startsWith "m") {
			messageCount = line.split('.')(1).toLong
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
		case e: Exception => logger.warn(s"error processing line: $line, exception: $e")
	}

	private def handleChat(dotSplit: Array[String], line: String) {
		val innerSplit = line.split("\\.", 4)
		val nick = innerSplit(1)
		val msg = getZagramDecoded(innerSplit(3)).toLowerCase
		if (msg.startsWith("!register") ||
				msg.startsWith("!register") ||
				msg.startsWith("!register")) {
			playerSet.get(nick) match {
				case Some(info) =>
					logger.info(s"registration attempt caught: $info")
					context.parent ! TryRegister(info)
				case _ =>
					logger.info(s"registration attempt failed: no user info for $nick")
					context.parent ! MessageToZagram(s"Sorry, could not find zagram rank for $nick")
			}
		}
	}

	private def handleGameState(dotSplit: Array[String]) {
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

	private def handleGameDescription(dotSplit: Array[String]) {
		val tableN = dotSplit(0).toInt
		val first = getZagramDecoded(dotSplit(dotSplit.length - 2))
		val second = getZagramDecoded(dotSplit(dotSplit.length - 1))
		gameSet += tableN.toString -> GameInfo(first, second)
	}

	private def handleUserInfo(dotSplit: Array[String]) {
		val player = dotSplit(0)
		val (rating, wins, losses, draws) =
			if (player startsWith "*")
				(0, 0, 0, 0)
			else
				(dotSplit(4).toInt, dotSplit(5).toInt, dotSplit(7).toInt, dotSplit(6).toInt)
		playerSet += player -> PlayerInfo(player, rating, wins, losses, draws)
	}

}
