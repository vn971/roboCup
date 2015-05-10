package ru.ya.vn91.robotour

import akka.actor._
import code.comet.TournamentStatus._
import code.comet._
import net.liftweb.common.Loggable
import ru.ya.vn91.robotour.Constants._
import ru.ya.vn91.robotour.Utils.SuppressWartRemover
import ru.ya.vn91.robotour.zagram._
import scala.collection.immutable.HashSet
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

class KnockoutCore extends Actor with Loggable {

	object StartTheTournament
	object StartNextTour


	var waiting = HashSet[GameNode]()
	var playing = HashSet[(GameNode, GameNode)]() // each inner set must contain 2 players
	var knockedOut = HashSet[GameNode]()

	override def preStart(): Unit = {
		context.actorOf(Props(new FromZagram(self)), name = "fromZagram").suppressWartRemover()
		logger.info("initialized")
	}

	def prepareNextTour(): Unit = {
		logger.info("prepare next tour")
		if (playing.size > 0) throw new IllegalStateException
		else if (waiting.size < 2) {
			logger.info("tournament finished!")
			if (waiting.size == 1) {
				logger.info(s"winner: ${waiting.head}")
				GlobalStatusSingleton ! FinishedWithWinner(waiting.head.name)
			} else {
				logger.info("Draw!")
				GlobalStatusSingleton ! FinishedWithDraw
			}
			logger.info(s"knockedOut: $knockedOut")
			context.become(receive, discardOld = true)
		} else {
			logger.info("shuffling and assigning games")
			val shuffled = toVectorAndShuffle(waiting)

			val lesserPower2 = List(512, 256, 128, 64, 32, 16, 8, 4, 2, 1).find(_ < shuffled.size).getOrElse(sys.error(""))
			val greaterPower2 = lesserPower2 * 2

			for (i <- lesserPower2 until shuffled.size) {
				val (i1, i2) = (i, greaterPower2 - 1 - i) // indices
				val (p1, p2) = (shuffled(i1), shuffled(i2)) // players
				logger.info(s"assigning game ${p1.name}-${p2.name}")
				playing += ((p1, p2))
				Core.toZagramActor ! AssignGame(p1.name, p2.name)

				if (Random.nextBoolean()) {
					logger.info(s"preparing winner in case of timeout: ${p1.name}")
					context.system.scheduler.scheduleOnce(gameTimeout, self, GameWon(p1.name, p2.name))
				} else {
					logger.info(s"preparing winner in case of timeout: ${p2.name}")
					context.system.scheduler.scheduleOnce(gameTimeout, self, GameWon(p2.name, p1.name))
				}
			}
			waiting = HashSet.empty
			for (j <- 0 until greaterPower2 - shuffled.size) {
				waiting += GameNode(shuffled(j).name, shuffled(j))
			}

			logger.info(s"waiting = $waiting")
			logger.info(s"playing = $playing")
			WaitingSingleton ! waiting.toList
			PlayingSingleton ! playing.toList

			context.become(inProgress, discardOld = true)
		}

	}

	def registration: Receive = {
		case TryRegister(info) =>
			if (waiting.forall(_.name != info.nick)) {
				logger.info(s"registered ${info.nick}")
				waiting += GameNode(info.nick)
				RegisteredListSingleton ! info.nick
				ChatServer ! MessageToChatServer(s"Player ${info.nick} registered.")
				WaitingSingleton ! waiting.toList
			}

		case StartTheTournament =>
			logger.info("Tournament started!")
			GlobalStatusSingleton ! GamePlaying(0)
			prepareNextTour()
			context.become(inProgress, discardOld = true)

	}

	def inProgress: Receive = {
		case GameWon(winner, looser) =>
			val containsWinnerLooser = {
				val filter1 = playing.filter(g => g._1.name == winner && g._2.name == looser)
				playing --= filter1
				waiting ++= filter1.map(g => GameNode(winner, g._1, g._2))
				knockedOut ++= filter1.map(_._2)
				filter1.size != 0
			}
			val containsLooserWinner = {
				val filter2 = playing.filter(g => g._1.name == looser && g._2.name == winner)
				playing --= filter2
				waiting ++= filter2.map(g => GameNode(winner, g._1, g._2))
				knockedOut ++= filter2.map(_._1)
				filter2.size != 0
			}
			if (containsWinnerLooser || containsLooserWinner) {
				logger.info(s"game won: $winner > $looser")
				ChatServer ! MessageToChatServer(s"$winner has won a game against $looser")
				WaitingSingleton ! waiting.toList
				PlayingSingleton ! playing.toList
				KnockedOutSingleton ! knockedOut.toList
				if (playing.size == 0) {
					if (waiting.size < 2) {
						logger.info("last game played. Calculating tournament result now.")
						prepareNextTour()
					} else {
						context.become(waitingNextTour, discardOld = true)
						context.system.scheduler.scheduleOnce(breakTime, self, StartNextTour).suppressWartRemover()
						logger.info("starting tournament break now.")
						GlobalStatusSingleton ! WaitingForNextTour(System.currentTimeMillis + breakTime.toMillis)
					}
				}
			}

		case GameDraw(first, second) =>
			if (Random.nextBoolean())
				self ! GameWon(first, second)
			else
				self ! GameWon(second, first)
	}

	def waitingNextTour: Receive = {
		case StartNextTour =>
			logger.info("starting next tour.")
			context.become(inProgress, discardOld = true)
			GlobalStatusSingleton ! GamePlaying(0)
			prepareNextTour()
	}

	def receive = {
		case StartRegistration(time) =>
			logger.info(s"registration assigned, time: $time")
			TimeStartSingleton ! time + registrationPeriod.toMillis // timeAsString
			if (System.currentTimeMillis < time) {
				logger.info("added suspended notify (registration start)")
				context.system.scheduler.scheduleOnce((time - System.currentTimeMillis).milliseconds, self, StartRegistration(time)).suppressWartRemover()
				GlobalStatusSingleton ! RegistrationAssigned(time)
			} else {
				logger.info("registration started!")
				context.system.scheduler.scheduleOnce(registrationPeriod + (time - System.currentTimeMillis).milliseconds, self, StartTheTournament).suppressWartRemover()
				waiting = HashSet.empty
				playing = HashSet.empty
				knockedOut = HashSet.empty
				GlobalStatusSingleton ! RegistrationInProgress(time + registrationPeriod.toMillis)
				context.become(registration, discardOld = true)
			}
	}

	def toVectorAndShuffle[T](set: Set[T]) = {
		val buffer = set.toBuffer

		def transpose(i1: Int, i2: Int): Unit = { // transpose two elements in list
			val temp = buffer(i1)
			buffer.update(i1, buffer(i2))
			buffer.update(i2, temp)
		}

		for (i <- 0 to buffer.size - 2) {
			val transposeWith = i + Random.nextInt(buffer.size - i)
			transpose(i, transposeWith)
		}
		buffer.toVector
	}

}
