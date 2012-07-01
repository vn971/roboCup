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

private object StartTheTournament
private case class StartRegistrationReally(val time: Long)

trait RegistrationCore extends Actor {

	val log = Logging(context.system, RegistrationCore.this)

	val toZagram = context.actorOf(Props[ToZagram], name = "toZagram")

	val registered = LinkedHashSet[String]()

	override def preStart() = {
		val fromZagram = context.actorOf(Props[FromZagram], name = "fromZagram")
	}

	def afterRegistration(player: String): Unit

	def receive = {
		case StartRegistration(time) =>
			log.info("StartRegistration")
			context.become(registartionAssigned, true)
			context.system.scheduler.scheduleOnce(time - System.currentTimeMillis milliseconds, self, StartRegistrationReally(time))
			GlobalStatusSingleton ! RegistrationAssigned(time)
	}

	def registartionAssigned: Receive = {
		case StartRegistrationReally(time) =>
			log.info("registrationStartedReally")
			context.become(registrationInProgress)

			context.system.scheduler.scheduleOnce(time + registrationLength - System.currentTimeMillis milliseconds, self, StartTheTournament)

			GlobalStatusSingleton ! RegistrationInProgress(time + registrationLength)
	}

	def doRegister(nick: String) = {
		log.info("registered "+nick)
		registered += nick
		RegisteredListSingleton ! nick
		ChatServer ! MessageFromAdmin("Player "+nick+" registered.")
		afterRegistration(nick)
	}

	def registrationInProgress: Receive = {
		case TryRegister(nick) => if (!registered.contains(nick)) doRegister(nick)
		// case StartTournament
		// this Receive function is extended by extending classes
	}

}