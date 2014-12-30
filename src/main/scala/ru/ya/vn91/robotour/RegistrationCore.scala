package ru.ya.vn91.robotour

import akka.actor.{ Actor, Props }
import code.comet.TournamentStatus.{ RegistrationAssigned, RegistrationInProgress }
import code.comet._
import net.liftweb.common.Loggable
import ru.ya.vn91.robotour.Constants._
import ru.ya.vn91.robotour.Utils.SuppressWartRemover
import ru.ya.vn91.robotour.zagram._
import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

private object StartTheTournament
private[robotour] case class StartRegistrationReally(time: Long)

trait RegistrationCore extends Actor with Loggable {

	val toZagram = context.actorOf(Props[ToZagram], name = "toZagram")

	context.actorOf(Props[FromZagram], name = "fromZagram").suppressWartRemover()

	var registered = immutable.HashSet[String]()

	def receive = {
		case StartRegistration(time) =>
			logger.info("StartRegistration")
			context.become(registrationAssigned, discardOld = true)
			context.system.scheduler.scheduleOnce((time - System.currentTimeMillis).millis, self, StartRegistrationReally(time)).suppressWartRemover()
			GlobalStatusSingleton ! RegistrationAssigned(time)
			TimeStartSingleton ! time + registrationTime.toMillis // timeAsString
	}

	def registrationAssigned: Receive = {
		case StartRegistrationReally(time) =>
			logger.info("registrationStartedReally")
			context.become(registrationInProgress)

			context.system.scheduler.scheduleOnce(registrationTime + (time - System.currentTimeMillis).millis, self, StartTheTournament).suppressWartRemover()

			for (cgw <- Constants.createGameWith) {
				toZagram ! AssignGame("RoboCup", cgw, infiniteTime = true)
			}
			GlobalStatusSingleton ! RegistrationInProgress(time + registrationTime.toMillis)
	}

	protected def register(playerInfo: PlayerInfo) {
		if (!registered.contains(playerInfo.nick)) {
			logger.info(s"registered ${playerInfo.nick}")
			registered += playerInfo.nick
			RegisteredListSingleton ! playerInfo.nick
			ChatServer ! MessageToChatServer(s"Player ${playerInfo.nick} registered.")
		}
	}

	def registrationInProgress: Receive = {
		case TryRegister(info) => if (!registered.contains(info.nick)) register(info)
		// this Receive function is extended by extending classes
		// (case StartTournament)
	}

}
