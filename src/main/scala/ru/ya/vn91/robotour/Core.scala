package ru.ya.vn91.robotour

import akka.actor._
import akka.event.Logging
import akka.util.duration._
import collection.mutable._
import util.Random

class TryRegister(val player: String)
class GameFinished(val winner: String, val looser: String)
object StartRegistration
object StartTheTournament
object StartNextTour

class Core(httpUrl: String, tournamentStart: Long) extends Actor {

	val tourBrakeTime = 1000L * 60 * 5

	val toZagram = context.actorOf(Props[ToZagram], name = "toZagram")
	val fromZagram = context.actorOf(Props[FromZagram], name = "fromZagram")
	val log = Logging(context.system, this)

	val waiting = LinkedHashSet[GameNode]()
	val playing = LinkedHashSet[(GameNode, GameNode)]() // each inner set must have contain 2 players
	val knockedOut = LinkedHashSet[GameNode]()

	def timeToString() = ""

	def sendToMyself(timeout: Long, event: Any, executeIfLate: Boolean = false) = {
		context.actorOf(Props(new Notifier(timeout, event)))
	}

	override def preStart() = {
		println(toListAndShuffle(Seq(1, 2, 3, 4, 5, 6, 7, 8)))

		sendToMyself(tournamentStart - 1000L * 60 * 60 * 3, "The tournament will start in 3 hours! "+httpUrl)
		sendToMyself(tournamentStart - 1000L * 60 * 60, "The tournament will start in 1 hour! "+httpUrl)
		sendToMyself(tournamentStart, "The tournament is starting NOW!")

		sendToMyself(tournamentStart - 1000L * 60 * 60 * 3, StartRegistration)

		sendToMyself(tournamentStart - 1000L * 60 * 60 * 3, StartTheTournament)
	}

	def prepareNextTour = {
		if (playing.size > 0) throw new IllegalStateException
		else if (waiting.size < 2) {
			log.info("tournament finished!")
			if (waiting.size == 1) log info "winner: \n"+waiting.head
			log info "participants: \n"+knockedOut
			// context.system.shutdown()
			context.become(finished)
		} else {
			val time = System.currentTimeMillis
			val shuffled = toListAndShuffle(waiting.toSeq)
			for (i <- 0 to waiting.size / 2 - 1) {
				val (p1, p2) = (shuffled(2 * i), shuffled(2 * i + 1))
				playing += ((p1, p2))
				toZagram ! new AssignGame(p1.name, p2.name)
				sendToMyself(time, new GameFinished(p1.name, p2.name))
			}

			waiting.clear
			if (shuffled.size % 2 != 0) {
				waiting += new Branch(shuffled.last.name, List(shuffled.last))
			}

			log.info("waiting = \n"+waiting)
			log.info("playing = \n"+playing)
			context.become(inProgress)
		}

	}

	def finished: Receive = {
		case _ =>
	}

	def registration: Receive = {
		case s: String => toZagram ! s
		case registration: TryRegister => {
			//			log.info("tried to register: "+registration.player)
			//			log.info("waiting before reg: "+waiting)
			if (waiting.forall(_.name != registration.player)) {
				waiting += Leaf(registration.player)
				log.info("registered player: "+registration.player)
				toZagram ! "player"+registration.player+"registered."
			}
		}
		case StartTheTournament => {
			log.info("Starting the tournament")
			prepareNextTour
			context.become(inProgress)
		}
	}

	def inProgress: Receive = {
		case s: String => toZagram ! s
		case f: GameFinished => {
			log.info("f.winner = "+f.winner+", f.looser = "+f.looser)

			{
				val filter1 = playing.filter(g => g._1.name == f.winner && g._2.name == f.looser)
				playing --= filter1
				waiting ++= filter1.map(g => Branch(f.winner, List(g._1, g._2)))
				knockedOut ++= filter1.map(_._2)
				//				filter1.foreach { g => waiting += Branch(f.winner, List(g._1, g._2)) }
			}
			{
				val filter2 = playing.filter(g => g._1.name == f.looser && g._2.name == f.winner)
				playing --= filter2
				waiting ++= filter2.map(g => Branch(f.winner, List(g._1, g._2)))
				knockedOut ++= filter2.map(_._1)
				//				filter2.foreach { g => waiting += Branch(f.winner, List(g._1, g._2)) }
			}
			log.info("playing after game calculation = "+playing)
			log.info("waiting after game calculation = "+waiting)
			if (playing.size == 0 && waiting.size < 2) {
				prepareNextTour
			} else if (playing.size == 0) {
				context.become(waitingNextTour)
				toZagram ! "Next Tour will start in "+(tourBrakeTime / 1000 / 60)+" minutes "+httpUrl
				sendToMyself(System.currentTimeMillis + tourBrakeTime, StartNextTour)
			}
		}
	}

	def waitingNextTour: Receive = {
		case s: String => toZagram ! s
		case StartNextTour => {
			context.become(inProgress)
			prepareNextTour
		}
	}

	def receive = {
		case s: String => toZagram ! s
		case StartRegistration => context.become(registration)
	}

	def toListAndShuffle[T](set: collection.Seq[T]): List[T] = {
		val buffer = set.toBuffer[T]

		def transpose(i1: Int, i2: Int) = { // transpose two elements in list
			val temp = buffer(i1)
			buffer.update(i1, buffer(i2))
			buffer.update(i2, temp)
		}

		for (i <- 0 to buffer.size - 2) {
			val transposeWith = i + Random.nextInt(buffer.size - i)
			transpose(i, transposeWith)
		}
		buffer.toList
	}

	//	def pickRandom[T](set: Set[T]): T = set.toList((math.random * set.size).toInt)
}
