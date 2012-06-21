package ru.ya.vn91.robotour

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging

trait SendToMyself extends Actor {
	def sendToMyself(timeout: Long, event: Any, executeIfLate: Boolean = false): Unit = {
		context.actorOf(Props(new Notifier(timeout, event, executeIfLate)))
	}

	private class Notifier(timeout: Long, message: Any, executeIfLate: Boolean = false) extends Actor {

		override def preStart() = {
			val timeLeft = timeout - System.currentTimeMillis
			if (timeLeft > 0) {
				Thread.sleep(timeLeft)
				context.parent ! message
			} else if (executeIfLate) {
				context.parent ! message
			}
			context.stop(self)
		}

		def receive = {
			case _ =>
		}
	}

}