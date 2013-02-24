package ru.ya.vn91.robotour

import akka.actor.ActorSystem
import net.liftweb.common.Loggable

case class TryRegister(val info: PlayerInfo)
case class GameWon(val winner: String, val looser: String)
case class GameDraw(val first: String, val second: String)
case class StartRegistration(val timeStart: Long)

object Core extends Loggable {
	val system = ActorSystem("robo")
	val core = if (Constants.isKnockout)
		system.actorOf(akka.actor.Props[KnockoutCore], name = "core")
	else
		system.actorOf(akka.actor.Props[SwissCore], name = "core")

	logger info "starting tournament "+Constants.tournamentName
}
