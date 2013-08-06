package ru.ya.vn91.robotour

import akka.actor.ActorSystem
import net.liftweb.common.Loggable
import ru.ya.vn91.robotour.zagram.PlayerInfo

case class TryRegister(info: PlayerInfo)
case class GameWon(winner: String, looser: String)
case class GameDraw(first: String, second: String)
case class StartRegistration(timeStart: Long)

object Core extends Loggable {
	val system = ActorSystem("robo")
	val core = if (Constants.isSwiss)
		system.actorOf(akka.actor.Props[SwissCore], name = "core")
	else
		system.actorOf(akka.actor.Props[KnockoutCore], name = "core")

	logger.info(s"starting tournament ${Constants.tournamentCodename}")
}
