package ru.ya.vn91.robotour

import akka.actor.ActorSystem

case class TryRegister(val nick: String)
case class GameWon(val winner: String, val looser: String)
case class GameDraw(val first: String, val second: String)
case class StartRegistration(val timeStart: Long)

object Core {
	val system = ActorSystem("robo")
	val core = if (Constants.isKnockout)
		system.actorOf(akka.actor.Props[KnockoutCore], name = "core")
	else
		system.actorOf(akka.actor.Props[SwissCore], name = "core")
}