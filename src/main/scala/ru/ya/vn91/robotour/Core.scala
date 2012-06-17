package ru.ya.vn91.robotour

import akka.actor._
import akka.event.Logging
import akka.util.duration._
import collection.mutable._
import util.Random
import code.comet.{ ChatServer, WaitingSingleton, KnockedOutSingleton, PlayingSingleton }
import code.comet.RegisteredListSingleton
import code.comet.TimeStartSingleton
import code.comet.GlobalStatusSingleton
import code.comet.status._
import Constants._
import code.comet.MessageFromAdmin

case class TryRegister(val nick: String)
case class GameFinished(val winner: String, val looser: String)
//case class MessageFromZagram(val time: Long, val nick: String, val message: String)
case class StartRegistration(val timeStart: Long)
object StartTheTournament
object StartNextTour

class Core extends Actor {

	val toZagram = context.actorOf(Props[ToZagram], name = "toZagram")
	val fromZagram = context.actorOf(Props[FromZagram], name = "fromZagram")
	val log = Logging(context.system, this)

	val waiting = LinkedHashSet[GameNode]()
	val playing = LinkedHashSet[(GameNode, GameNode)]() // each inner set must contain 2 players
	val knockedOut = LinkedHashSet[GameNode]()

	def sendToMyself(timeout: Long, event: Any, executeIfLate: Boolean = false): Unit = {
		context.actorOf(Props(new Notifier(timeout, event, executeIfLate)))
	}

	override def preStart() = { log info "inited" }

	def prepareNextTour = {
		log.info("prepare next tour.")
		if (playing.size > 0) throw new IllegalStateException
		else if (waiting.size < 2) {
			ChatServer ! MessageFromAdmin("Турнир закончен!")
			log.info("tournament finished!")
			if (waiting.size == 1) {
				log info "winner: \n"+waiting.head
				ChatServer ! MessageFromAdmin("Победитель: "+waiting.head.name)
				GlobalStatusSingleton ! FinishedWithWinner(waiting.head.name)
			} else {
				log info "Draw!"
				ChatServer ! MessageFromAdmin("Результат: ничья!")
				GlobalStatusSingleton ! FinishedWithDraw
			}
			log info "knockedOut: \n"+knockedOut
			context.become(receive, true)
		} else {
			log info "shuflling and assigning games"
			val time = System.currentTimeMillis
			val shuffled = toListAndShuffle(waiting.toSeq)

			val lesserPower2 = List(512, 256, 128, 64, 32, 16, 8, 4, 2, 1).find(_ < shuffled.size).get
			val greaterPower2 = lesserPower2 * 2

			for (i <- lesserPower2 until shuffled.size) yield {
				val (i1, i2) = (i, greaterPower2 - 1 - i) // indexes
				val (p1, p2) = (shuffled(i1), shuffled(i2)) // players
				playing += ((p1, p2))
				toZagram ! new AssignGame(p1.name, p2.name)

				// random winner in case of timeout
				if (Random.nextBoolean)
					sendToMyself(time + gameTimeout, new GameFinished(p2.name, p1.name))
				else
					sendToMyself(time + gameTimeout, new GameFinished(p1.name, p2.name))
			}
			waiting.clear
			for (j <- 0 until greaterPower2 - shuffled.size) yield {
				waiting += new Branch(shuffled(j).name, shuffled(j) :: Nil)
			}

			log.info("waiting = \n"+waiting)
			log.info("playing = \n"+playing)
			WaitingSingleton ! waiting.toList
			PlayingSingleton ! playing.toList

			GlobalStatusSingleton ! GamePlaying(0)
			context.become(inProgress, true)
		}

	}

	def registration: Receive = {
		case TryRegister(player) => {
			if (waiting.forall(_.name != player)) {
				log.info("registered "+player)
				waiting += Leaf(player)
				RegisteredListSingleton ! player
				ChatServer ! MessageFromAdmin("игрок "+player+" зарегистрировался.")
				WaitingSingleton ! waiting.toList
			}
		}
		case StartTheTournament => {
			log.info("Tournament started!")
			ChatServer ! MessageFromAdmin("Турнир начался!")
			GlobalStatusSingleton ! GamePlaying(0)
			prepareNextTour
			context.become(inProgress, true)
		}
	}

	def inProgress: Receive = {
		case GameFinished(winner, looser) => {
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
				ChatServer ! MessageFromAdmin(winner+" выиграл игру против "+looser)
				WaitingSingleton ! waiting.toList
				PlayingSingleton ! playing.toList
				KnockedOutSingleton ! knockedOut.toList
				if (playing.size == 0) {
					if (waiting.size < 2) {
						log.info("last game played. Calculating tournament result now.")
						prepareNextTour
					} else {
						context.become(waitingNextTour, true)
						sendToMyself(System.currentTimeMillis + tourBrakeTime, StartNextTour)
						log.info("starting tournament break now.")
						ChatServer ! MessageFromAdmin("Следующий тур начнётся через "+(tourBrakeTime / 1000 / 60)+" минут.")
						GlobalStatusSingleton ! WaitingForNextTour(System.currentTimeMillis + tourBrakeTime)
					}
				}
			}
		}
	}

	def waitingNextTour: Receive = {
		case StartNextTour => {
			log.info("starting next tour.")
			context.become(inProgress, true)
			ChatServer ! MessageFromAdmin("Начался следующий тур!")
			GlobalStatusSingleton ! GamePlaying(0)
			prepareNextTour
		}
	}

	def receive = {
		case StartRegistration(time) =>
			log.info("registratin assigned.")
			TimeStartSingleton ! time + registrationLength // timeAsString
			if (System.currentTimeMillis < time) {
				log.info("added suspended notify (registration start)")
				sendToMyself(time, StartRegistration(time), true)
				GlobalStatusSingleton ! RegistrationAssigned(time)
			} else {
				log info "registration started!"
				sendToMyself(time + registrationLength, StartTheTournament, true)
				waiting.clear
				playing.clear
				knockedOut.clear
				ChatServer ! MessageFromAdmin("Регистрация открыта!")
				GlobalStatusSingleton ! RegistrationInProgress(time + registrationLength)
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

	//	def pickRandom[T](set: Set[T]): T = set.toList((math.random * set.size).toInt)
}

object Core {
	val system = ActorSystem("robo")
	val core = system.actorOf(akka.actor.Props[Core], name = "core")
}
