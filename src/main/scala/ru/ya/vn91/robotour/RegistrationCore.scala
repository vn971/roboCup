package ru.ya.vn91.robotour

import akka.actor.Actor
import akka.actor.Props
import code.comet.ChatServer
import code.comet.GlobalStatusSingleton
import code.comet.MessageFromAdmin
import code.comet.RegisteredListSingleton
import code.comet.TimeStartSingleton
import code.comet.status._
import net.liftweb.common.Loggable
import ru.ya.vn91.robotour.Constants._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


private object StartTheTournament
private case class StartRegistrationReally(time: Long)

trait RegistrationCore extends Actor with Loggable {

	val toZagram = context.actorOf(Props[ToZagram], name = "toZagram")

	val registered = mutable.LinkedHashSet[String]()

	override def preStart() {
		context.actorOf(Props[FromZagram], name = "fromZagram")
	}

	def receive = {
		case StartRegistration(time) =>
			logger.info("StartRegistration")
			context.become(registartionAssigned, discardOld = true)
			context.system.scheduler.scheduleOnce((time - System.currentTimeMillis).millis, self, StartRegistrationReally(time))
			GlobalStatusSingleton ! RegistrationAssigned(time)
			TimeStartSingleton ! time + registrationMillis // timeAsString
	}

	def registartionAssigned: Receive = {
		case StartRegistrationReally(time) =>
			logger.info("registrationStartedReally")
			context.become(registrationInProgress)

			context.system.scheduler.scheduleOnce((time + registrationMillis - System.currentTimeMillis).millis, self, StartTheTournament)

			toZagram ! AssignGame("RoboCup", organizatorNickname, sayHiTime = 7000)
			GlobalStatusSingleton ! RegistrationInProgress(time + registrationMillis)
	}

	def register(playerInfo: PlayerInfo) {
		if (!registered.contains(playerInfo.nick)) {
			logger.info("registered "+playerInfo.nick)
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