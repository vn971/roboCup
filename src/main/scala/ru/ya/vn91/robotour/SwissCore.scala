package ru.ya.vn91.robotour

import akka.actor.Actor

class SwissCore extends Actor with RegistrationCore {

	override def registrationInProgress =
		super.registrationInProgress.orElse {
			case StartTheTournament =>
		}
}