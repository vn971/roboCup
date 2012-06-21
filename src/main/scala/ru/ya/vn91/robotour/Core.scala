package ru.ya.vn91.robotour

import akka.actor.ActorSystem

case class TryRegister(val nick: String)
case class GameFinished(val winner: String, val looser: String)
case class StartRegistration(val timeStart: Long)

object Core {
	val system = ActorSystem("robo")
	val core = system.actorOf(akka.actor.Props[KnockoutCore], name = "core")
}