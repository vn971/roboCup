package ru.ya.vn91.robotour

import scala.util.Random
import akka.actor.Props
import akka.event.Logging
import akka.util.duration._
import Constants._

class SwissCore extends RegistrationCore {

	val toZagram = context.actorOf(Props[ToZagram], name = "toZagram")
	val fromZagram = context.actorOf(Props[FromZagram], name = "fromZagram")

	val scores = collection.mutable.LinkedHashMap[String, Double]()

	def test = {
		val time = System.currentTimeMillis

		val sortedPlayers = scores.toList.sortBy(s => (s._2, Random.nextInt))

		for (i <- 0.until(sortedPlayers.length, 2)) {
			val first = sortedPlayers(i)._1
			val second = sortedPlayers(i + 1)._1
			log.info("assigning game "+first+"-"+second)
			toZagram ! AssignGame(first, second)
			context.system.scheduler.scheduleOnce(gameTimeout milliseconds, self, GameDraw(first, second))
			//			sendToMyself(time + gameTimeout, GameDraw(first, second))
		}
	}

	override def registrationInProgress =
		super.registrationInProgress.orElse {
			case StartTheTournament =>
				log.info("starting tournament")
				if (registered.size % 2 != 0) {
					// Swiss tournament needs an even number of players
					registered += "Empty"
				}
				registered.foreach(s => scores += (s -> s.apply(0).toInt))
				test
		}
}