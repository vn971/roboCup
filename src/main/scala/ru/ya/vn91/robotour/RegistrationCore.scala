package ru.ya.vn91.robotour

import akka.actor.Actor
import akka.actor.Props
import akka.util.duration._
import scala.collection.mutable.LinkedHashSet
import code.comet.RegisteredListSingleton
import code.comet.ChatServer
import code.comet.MessageFromAdmin
import code.comet.WaitingSingleton
import akka.event.Logging
import ru.ya.vn91.robotour.Constants._
import code.comet.GlobalStatusSingleton
import code.comet.status._
import code.comet.TimeStartSingleton

private object StartTheTournament
private case class StartRegistrationReally(val time: Long)

trait RegistrationCore extends Actor {

	val log = Logging(context.system, RegistrationCore.this)

	val toZagram = context.actorOf(Props[ToZagram], name = "toZagram")

	val registered = LinkedHashSet[String]()

	override def preStart() = {
		val fromZagram = context.actorOf(Props[FromZagram], name = "fromZagram")
	}

	def receive = {
		case StartRegistration(time) =>
			log.info("StartRegistration")
			context.become(registartionAssigned, true)
			context.system.scheduler.scheduleOnce(time - System.currentTimeMillis milliseconds, self, StartRegistrationReally(time))
			GlobalStatusSingleton ! RegistrationAssigned(time)
			TimeStartSingleton ! time + registrationMillis // timeAsString
	}

	def registartionAssigned: Receive = {
		case StartRegistrationReally(time) =>
			log.info("registrationStartedReally")
			context.become(registrationInProgress)

			context.system.scheduler.scheduleOnce(time + registrationMillis - System.currentTimeMillis milliseconds, self, StartTheTournament)

			toZagram ! AssignGame("RoboCup", organizatorNickname, sayHiTime = 7000)
			GlobalStatusSingleton ! RegistrationInProgress(time + registrationMillis)
	}

	def register(playerInfo: PlayerInfo) = {
		if (!registered.contains(playerInfo.nick)) {
			log.info("registered "+playerInfo.nick)
			registered += playerInfo.nick
			RegisteredListSingleton ! playerInfo.nick
			ChatServer ! MessageFromAdmin("Player "+playerInfo.nick+" registered.")
		}
	}

	def registrationInProgress: Receive = {
		case TryRegister(info) => if (!registered.contains(info.nick)) register(info)
		// this Receive function is extended by extending classes
		// (case StartTournament)
	}

}