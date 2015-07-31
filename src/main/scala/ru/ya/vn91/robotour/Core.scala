package ru.ya.vn91.robotour

import akka.actor.ActorSystem
import net.liftweb.common.Loggable
import ru.ya.vn91.robotour.zagram.{ PlayerInfo, ToZagram }

case class TryRegister(info: PlayerInfo)
case class GameWon(winner: String, looser: String)
case class GameDraw(first: String, second: String)
case class StartRegistration(timeStart: Long)

object Core extends Loggable {
	def init() = ()

	logger.info(s"starting tournament ${Constants.tournamentCodename}")

	val system = ActorSystem("robocup")
	val core = if (Constants.isSwiss)
		system.actorOf(akka.actor.Props[SwissCore], name = "core")
	else
		system.actorOf(akka.actor.Props[KnockoutCore], name = "core")

	// hack-ish work-around
	core ! StartRegistration(Constants.registrationStartDate.getMillis)

	val toZagramActor = system.actorOf(akka.actor.Props[ToZagram], name = "toZagram")
}
