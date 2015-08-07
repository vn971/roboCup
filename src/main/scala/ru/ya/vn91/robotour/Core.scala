package ru.ya.vn91.robotour

import akka.actor.ActorSystem
import code.comet.ChatServer
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

	val chatServer = new ChatServer()
	val toZagramActor = system.actorOf(akka.actor.Props(new ToZagram), name = "toZagram")

	val core = if (Constants.isSwiss) {
		system.actorOf(akka.actor.Props.apply(new SwissCore(chatServer, toZagramActor)), name = "core")
	} else {
		system.actorOf(akka.actor.Props(new KnockoutCore(chatServer, toZagramActor)), name = "core")
	}
	core ! StartRegistration(Constants.registrationStartDate.getMillis) // hack-ish work-around

}
