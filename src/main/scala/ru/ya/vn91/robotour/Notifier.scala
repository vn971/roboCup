package ru.ya.vn91.robotour

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging

class Notifier(timeout: Long, message: Any, executeIfLate: Boolean = false) extends Actor {

	override def preStart() = {
		log.debug("actor inited")
		val timeLeft = timeout - System.currentTimeMillis
		if (timeLeft > 0) {
			Thread.sleep(timeLeft)
			context.parent ! message
		} else if (executeIfLate) {
			context.parent ! message
		}
		//			if (timeLeft > 0 || executeIfLate) {
		//			}
		context.stop(self)
	}

	val log = Logging(context.system, this)

	def receive = {
		case _ => Unit
	}
}