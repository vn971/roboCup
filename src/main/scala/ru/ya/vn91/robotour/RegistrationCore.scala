package ru.ya.vn91.robotour

import akka.actor.Actor
import akka.actor.Props
import scala.collection.mutable.LinkedHashSet
import code.comet.RegisteredListSingleton
import code.comet.ChatServer
import code.comet.MessageFromAdmin
import code.comet.WaitingSingleton
import akka.event.Logging

private object StartTheTournament
private object StartNextTour
private case class StartRegistrationReally(val time: Long)

trait RegistrationCore extends Actor with SendToMyself {
	val log = Logging(context.system, RegistrationCore.this)

	val registered = LinkedHashSet[String]()

	def receive: Receive = {
		case StartRegistration(time) =>
			context.become(registartionAssigned, true)
			sendToMyself(time, StartRegistrationReally(time), true)
	}

	def registartionAssigned: Receive = {
		case StartRegistrationReally(time) =>
			context.become(registrationInProgress)
			sendToMyself(time + Constants.registrationLength, StartTheTournament, true)
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