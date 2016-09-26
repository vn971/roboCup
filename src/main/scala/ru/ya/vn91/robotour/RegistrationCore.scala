package ru.ya.vn91.robotour

import akka.actor.Actor
import net.liftweb.common.Loggable
import ru.ya.vn91.lift.comet.TournamentStatus.{RegistrationAssigned, RegistrationInProgress}
import ru.ya.vn91.lift.comet._
import ru.ya.vn91.robotour.Constants._
import ru.ya.vn91.robotour.Utils.SuppressWartRemover
import ru.ya.vn91.robotour.zagram._
import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

private object StartTheTournament
private[robotour] case class StartRegistrationReally(time: Long)

trait RegistrationCore extends Actor with Loggable {

	var registered = immutable.HashSet[String]()

	def receive = {
		case StartRegistration(time) =>
			logger.info("StartRegistration")
			context.become(registrationAssigned, discardOld = true)
			context.system.scheduler.scheduleOnce((time - System.currentTimeMillis).millis, self, StartRegistrationReally(time)).suppressWartRemover()
			GlobalStatusSingleton ! RegistrationAssigned(time)
	}

	def registrationAssigned: Receive = {
		case StartRegistrationReally(time) =>
			logger.info("registrationStartedReally")
			context.become(registrationInProgress)

			context.system.scheduler.scheduleOnce(registrationPeriod + (time - System.currentTimeMillis).millis, self, StartTheTournament).suppressWartRemover()

			for (cgw <- Constants.createGameWith) {
				ToZagram.assignGame("RoboCup", cgw, isInfiniteTime = true)
			}
			GlobalStatusSingleton ! RegistrationInProgress(time + registrationPeriod.toMillis)
	}

	protected def tryRegister(p: PlayerInfo): Unit

	def registrationInProgress: Receive = {
		case TryRegister(info) => tryRegister(info)
		// this Receive function is extended by extending classes
		// (case StartTournament)
	}

}
