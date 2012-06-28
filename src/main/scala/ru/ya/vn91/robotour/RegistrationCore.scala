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

private object StartTheTournament
private object StartNextTour
private case class StartRegistrationReally(val time: Long)

trait RegistrationCore extends Actor with SendToMyself {
	val log = Logging(context.system, RegistrationCore.this)

	val registered = LinkedHashSet[String]()

	def receive: Receive = {
		case StartRegistration(time) =>
			log.info("StartRegistration")
			context.become(registartionAssigned, true)
			context.system.scheduler.scheduleOnce(time - System.currentTimeMillis milliseconds, self, StartRegistrationReally(time))
		//			sendToMyself(time, StartRegistrationReally(time), true)
	}

	def registartionAssigned: Receive = {
		case StartRegistrationReally(time) =>
			log.info("registrationStartedReally")
			context.become(registrationInProgress)
			context.system.scheduler.scheduleOnce(time + registrationLength - System.currentTimeMillis milliseconds, self, StartTheTournament)
		//			sendToMyself(time + Constants.registrationLength, StartTheTournament, true)
	}

	def registrationInProgress: Receive = {
		case TryRegister(nick) =>
			if (registered.add(nick)) {
				log.info("registered "+nick)
				RegisteredListSingleton ! nick
				ChatServer ! MessageFromAdmin("игрок "+nick+" зарегистрировался.")
			}
	}

}