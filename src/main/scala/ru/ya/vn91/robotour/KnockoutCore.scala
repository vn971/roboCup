package ru.ya.vn91.robotour

import akka.actor._
import akka.event.Logging
import akka.util.duration._
import akka.util.Duration
import collection.mutable._
import util.Random
import code.comet.ChatServer
import code.comet.{ WaitingSingleton, KnockedOutSingleton, PlayingSingleton }
import code.comet.RegisteredListSingleton
import code.comet.TimeStartSingleton
import code.comet.GlobalStatusSingleton
import code.comet.status._
import Constants._
import code.comet.MessageFromAdmin

class KnockoutCore extends Actor {

	object StartTheTournament
	object StartNextTour

	val toZagram = context.actorOf(Props[ToZagram], name = "toZagram")
	val fromZagram = context.actorOf(Props[FromZagram], name = "fromZagram")
	val log = Logging(context.system, KnockoutCore.this)

	val waiting = LinkedHashSet[GameNode]()
	val playing = LinkedHashSet[(GameNode, GameNode)]() // each inner set must contain 2 players
	val knockedOut = LinkedHashSet[GameNode]()

	override def preStart() = { log.info("inited") }

	def prepareNextTour = {
		log.info("prepare next tour.")
		if (playing.size > 0) throw new IllegalStateException
		else if (waiting.size < 2) {
			log.info("tournament finished!")
			if (waiting.size == 1) {
				log info "winner: \n"+waiting.head
				GlobalStatusSingleton ! FinishedWithWinner(waiting.head.name)
			} else {
				log info "Draw!"
				GlobalStatusSingleton ! FinishedWithDraw
			}
			log info "knockedOut: \n"+knockedOut
			context.become(receive, true)
		} else {
			log info "shuffling and assigning games"
			val time = System.currentTimeMillis
			val shuffled = toListAndShuffle(waiting.toSeq)

			val lesserPower2 = List(512, 256, 128, 64, 32, 16, 8, 4, 2, 1).find(_ < shuffled.size).get
			val greaterPower2 = lesserPower2 * 2

			for (i <- lesserPower2 until shuffled.size) yield {
				val (i1, i2) = (i, greaterPower2 - 1 - i) // indicies
				val (p1, p2) = (shuffled(i1), shuffled(i2)) // players
				log.info("assigning game "+p1.name+"-"+p2.name)
				playing += ((p1, p2))
				toZagram ! AssignGame(p1.name, p2.name)

				if (Random.nextBoolean) {
					log.info("preparing winner in case of timeout: "+p1.name)
					context.system.scheduler.scheduleOnce(gameTimeout milliseconds, self, GameWon(p1.name, p2.name))
				} else {
					log.info("preparing winner in case of timeout: "+p2.name)
					context.system.scheduler.scheduleOnce(gameTimeout milliseconds, self, GameWon(p2.name, p1.name))
				}
			}
			waiting.clear
			for (j <- 0 until greaterPower2 - shuffled.size) yield {
				waiting += new Branch(shuffled(j).name, shuffled(j) :: Nil)
			}

			log.info("waiting = \n"+waiting)
			log.info("playing = \n"+playing)
			WaitingSingleton ! waiting.toList
			PlayingSingleton ! playing.toList

			context.become(inProgress, true)
		}

	}

	def registration: Receive = {
		case TryRegister(info) => {
			if (info.nick startsWith "*") {
				toZagram ! MessageToZagram(info.nick+", to take a part in the tournament, please, use a registered account.")
			} else if (waiting.forall(_.name != info.nick)) {
				log.info("registered "+info.nick)
				waiting += Leaf(info.nick)
				RegisteredListSingleton ! info.nick
				ChatServer ! MessageFromAdmin("Player "+info.nick+" registered.")
				WaitingSingleton ! waiting.toList
			}
		}
		case StartTheTournament => {
			log.info("Tournament started!")
			GlobalStatusSingleton ! GamePlaying(0)
			prepareNextTour
			context.become(inProgress, true)
		}
	}

	def inProgress: Receive = {
		case GameWon(winner, looser) => {
			val containsWinnerLooser = {
				val filter1 = playing.filter(g => g._1.name == winner && g._2.name == looser)
				playing --= filter1
				waiting ++= filter1.map(g => Branch(winner, List(g._1, g._2)))
				knockedOut ++= filter1.map(_._2)
				filter1.size != 0
			}
			val containsLooserWinner = {
				val filter2 = playing.filter(g => g._1.name == looser && g._2.name == winner)
				playing --= filter2
				waiting ++= filter2.map(g => Branch(winner, List(g._1, g._2)))
				knockedOut ++= filter2.map(_._1)
				filter2.size != 0
			}
			if (containsWinnerLooser || containsLooserWinner) {
				log.info("game won: "+winner+" > "+looser)
				ChatServer ! MessageFromAdmin(winner+" has won a game against "+looser)
				WaitingSingleton ! waiting.toList
				PlayingSingleton ! playing.toList
				KnockedOutSingleton ! knockedOut.toList
				if (playing.size == 0) {
					if (waiting.size < 2) {
						log.info("last game played. Calculating tournament result now.")
						prepareNextTour
					} else {
						context.become(waitingNextTour, true)
						context.system.scheduler.scheduleOnce(tourBrakeTime milliseconds, self, StartNextTour)
						log.info("starting tournament break now.")
						GlobalStatusSingleton ! WaitingForNextTour(System.currentTimeMillis + tourBrakeTime)
					}
				}
			}
		}
		case GameDraw(first, second) =>
			if (Random.nextBoolean)
				self ! GameWon(first, second)
			else
				self ! GameWon(second, first)
	}

	def waitingNextTour: Receive = {
		case StartNextTour => {
			log.info("starting next tour.")
			context.become(inProgress, true)
			GlobalStatusSingleton ! GamePlaying(0)
			prepareNextTour
		}
	}

	def receive = {
		case StartRegistration(time) =>
			log.info("registratin assigned.")
			TimeStartSingleton ! time + registrationMillis // timeAsString
			if (System.currentTimeMillis < time) {
				log.info("added suspended notify (registration start)")
				context.system.scheduler.scheduleOnce(time - System.currentTimeMillis milliseconds, self, StartRegistration(time))
				GlobalStatusSingleton ! RegistrationAssigned(time)
			} else {
				log info "registration started!"
				context.system.scheduler.scheduleOnce(time + registrationMillis - System.currentTimeMillis milliseconds, self, StartTheTournament)
				waiting.clear
				playing.clear
				knockedOut.clear
				GlobalStatusSingleton ! RegistrationInProgress(time + registrationMillis)
				context.become(registration, true)
			}
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

}
