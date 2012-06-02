package ru.ya.vn91.robotour

import akka.actor._
import akka.event.Logging
import akka.util.duration._
import collection.mutable._
import util.Random
import code.comet.{ ChatServer, WaitingSingleton, KnockedOutSingleton, PlayingSingleton }
import code.comet.RegisteredListSingleton
import code.comet.ChatMessage
import code.comet.TimeStartSingleton
import code.comet.GlobalStatusSingleton
import code.comet.status._
import Constants._

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

	override def preStart() = {}

	def prepareNextTour = {
		if (playing.size > 0) throw new IllegalStateException
		else if (waiting.size < 2) {
			ChatServer ! ChatMessage("Турнир закончен!", 0L, "serv", "")
			log.info("tournament finished!")
			if (waiting.size == 1) {
				log info "winner: \n"+waiting.head
				ChatServer ! ChatMessage("Победитель: "+waiting.head.name, 0L, "serv", "")
				GlobalStatusSingleton ! FinishedWithWinner(waiting.head.name)
			} else {
				ChatServer ! ChatMessage("Результат: ничья!", 0L, "serv", "")
				GlobalStatusSingleton ! FinishedWithDraw
			}
			log info "knockedOut: \n"+knockedOut
			context.become(receive, true)
		} else {
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

			GlobalStatusSingleton ! GamePlaying
			context.become(inProgress, true)
		}

	}

	def registration: Receive = {
		case TryRegister(player) => {
			if (waiting.forall(_.name != player)) {
				waiting += Leaf(player)
				RegisteredListSingleton ! player
				ChatServer ! ChatMessage("игрок "+player+" зарегистрировался.", 0L, "serv", "")
				WaitingSingleton ! waiting.toList
			}
		}
		case StartTheTournament => {
			ChatServer ! ChatMessage("Турнир начался!", 0L, "serv", "")
			GlobalStatusSingleton ! GamePlaying
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
				ChatServer ! ChatMessage(winner+" выиграл игру против "+looser, 0L, "serv", "")
				WaitingSingleton ! waiting.toList
				PlayingSingleton ! playing.toList
				KnockedOutSingleton ! knockedOut.toList
				if (playing.size == 0) {
					if (waiting.size < 2) {
						prepareNextTour
					} else {
						context.become(waitingNextTour, true)
						ChatServer ! ChatMessage("Следующий тур начнётся через "+(tourBrakeTime / 1000 / 60)+" минут.", 0L, "serv", "")
						GlobalStatusSingleton ! WaitingForNextTour(System.currentTimeMillis + tourBrakeTime)
						sendToMyself(System.currentTimeMillis + tourBrakeTime, StartNextTour)
					}
				}
			}
		}
	}

	def waitingNextTour: Receive = {
		//		case s: String => toZagram ! s
		case StartNextTour => {
			context.become(inProgress, true)
			ChatServer ! ChatMessage("Начался следующий тур!", 0L, "serv", "")
			GlobalStatusSingleton ! GamePlaying
			prepareNextTour
		}
	}

	def receive = {
		case StartRegistration(time) =>
			TimeStartSingleton ! time + registrationLength // timeAsString
			if (System.currentTimeMillis < time) {
				sendToMyself(time, StartRegistration(time), true)
				GlobalStatusSingleton ! RegistrationAssigned(time)
			} else {
				sendToMyself(time + registrationLength, StartTheTournament, true)
				waiting.clear
				playing.clear
				knockedOut.clear
				ChatServer ! ChatMessage("Регистрация открыта!", 0L, "serv", "")
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
	private val system = ActorSystem("robo")
	val core = system.actorOf(akka.actor.Props[Core], name = "core")
}
