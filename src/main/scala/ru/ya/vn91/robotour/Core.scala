package ru.ya.vn91.robotour

import akka.actor._
import akka.event.Logging
import akka.util.duration._
import collection.mutable._
import util.Random
import code.comet.{ ChatServer, WaitingSingleton, KnockedOutSingleton, PlayingSingleton }
import code.comet.RegistrationSingleton
import code.comet.ChatMessage

case class TryRegister(val player: String)
case class GameFinished(val winner: String, val looser: String)
//case class MessageFromZagram(val time: Long, val nick: String, val message: String)
case class StartRegistration(val timeStart: Long)
object StartTheTournament
object StartNextTour

class Core extends Actor {

	val tourBrakeTime = 1000L * 60 * 10
	val gameTimeout = 1000L * 60 * 80

	val toZagram = context.actorOf(Props[ToZagram], name = "toZagram")
	val fromZagram = context.actorOf(Props[FromZagram], name = "fromZagram")
	val log = Logging(context.system, this)

	val waiting = LinkedHashSet[GameNode]()
	val playing = LinkedHashSet[(GameNode, GameNode)]() // each inner set must contain 2 players
	val knockedOut = LinkedHashSet[GameNode]()

	def timeToString() = ""

	def sendToMyself(timeout: Long, event: Any, executeIfLate: Boolean = false) = {
		context.actorOf(Props(new Notifier(timeout, event, executeIfLate)))
	}

	override def preStart() = {
		ChatServer ! ChatMessage(0L, "serv", "", "Инициализирована связь с заграмом.")

		//		sendToMyself(tournamentStart - 1000L * 60 * 60 * 3, ChatMessage() "The tournament will start in 3 hours! "+httpUrl)
		//		sendToMyself(tournamentStart - 1000L * 60 * 60, ChatMessage "The tournament will start in 1 hour! "+httpUrl)
		//		sendToMyself(tournamentStart, CHatMesage "The tournament is starting NOW!")

		//		sendToMyself(tournamentStart - 1000L * 60 * 60 * 3, StartRegistration, true)
		//		sendToMyself(tournamentStart, StartTheTournament, true)
	}

	def prepareNextTour = {
		if (playing.size > 0) throw new IllegalStateException
		else if (waiting.size < 2) {
			ChatServer ! ChatMessage(0L, "serv", "", "Турнир закончен!")
			log.info("tournament finished!")
			if (waiting.size == 1) {
				log info "winner: \n"+waiting.head
				ChatServer ! ChatMessage(0L, "serv", "", "Победитель: "+waiting.head.name)
			} else {
				ChatServer ! ChatMessage(0L, "serv", "", "Результат: ничья!")
			}
			log info "knockedOut: \n"+knockedOut
			context.become(receive)
		} else {
			val time = System.currentTimeMillis
			val shuffled = toListAndShuffle(waiting.toSeq)
			for (i <- 0 to waiting.size / 2 - 1) {
				val (p1, p2) = (shuffled(2 * i), shuffled(2 * i + 1))
				playing += ((p1, p2))
				toZagram ! new AssignGame(p1.name, p2.name)
				sendToMyself(time + gameTimeout, new GameFinished(p2.name, p1.name))
			}

			waiting.clear
			if (shuffled.size % 2 != 0) {
				waiting += new Branch(shuffled.last.name, List(shuffled.last))
			}

			//			log.info("waiting = \n"+waiting)
			//			log.info("playing = \n"+playing)
			WaitingSingleton ! waiting.toList
			PlayingSingleton ! playing.toList

			context.become(inProgress)
		}

	}

	def registration: Receive = {
		//		case s: String => toZagram ! s
		case TryRegister(player) => {
			//			log.info("tried to register: "+registration.player)
			//			log.info("waiting before reg: "+waiting)
			if (waiting.forall(_.name != player)) {
				waiting += Leaf(player)
				//				log.info("registered player: "+player)
				RegistrationSingleton ! player
				ChatServer ! ChatMessage(0L, "serv", "", "игрок "+player+" зарегистрировался.")
				WaitingSingleton ! waiting.toList
			}
		}
		case StartTheTournament => {
			ChatServer ! ChatMessage(0L, "serv", "", "Турнир начался!")
			prepareNextTour
			context.become(inProgress)
		}
	}

	def inProgress: Receive = {
		//		case s: String => toZagram ! s
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
				ChatServer ! ChatMessage(0L, "serv", "", winner+" выиграл игру против "+looser)
				//				log.info("playing after game calculation = "+playing)
				//				log.info("waiting after game calculation = "+waiting)
				WaitingSingleton ! waiting.toList
				PlayingSingleton ! playing.toList
				KnockedOutSingleton ! knockedOut.toList
				if (playing.size == 0) {
					if (waiting.size < 2) {
						prepareNextTour
					} else {
						context.become(waitingNextTour)
						ChatServer ! ChatMessage(0L, "serv", "", "Следующий тур начнётся через "+(tourBrakeTime / 1000 / 60)+" минут.")
						// toZagram ! "Next Tour will start in "+(tourBrakeTime / 1000 / 60)+" minutes "+httpUrl
						sendToMyself(System.currentTimeMillis + tourBrakeTime, StartNextTour)
					}
				}
			}
		}
	}

	def waitingNextTour: Receive = {
		//		case s: String => toZagram ! s
		case StartNextTour => {
			context.become(inProgress)
			ChatServer ! ChatMessage(0L, "serv", "", "Начался следующий тур!")
			prepareNextTour
		}
	}

	def receive = {
		case StartRegistration(time) =>
			if (System.currentTimeMillis < time) {
				sendToMyself(time, StartRegistration(time), true)
			} else {
				sendToMyself(time + 1000L * 60 * 60 * 3, StartTheTournament, true)
				waiting.clear
				playing.clear
				knockedOut.clear
				ChatServer ! ChatMessage(0L, "serv", "", "Регистрация открыта!")
				context.become(registration)
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
