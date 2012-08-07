package ru.ya.vn91.robotour

import akka.actor.actorRef2Scala
import akka.util.duration.longToDurationLong
import ru.ya.vn91.robotour.Constants._
import scala.util.Random
import scala.collection.mutable.ListBuffer
import code.comet.{ Game, Player, SwissTableData }
import code.comet.GameResultEnumeration._
import code.comet.SwissTableSingleton
import code.comet.GlobalStatusSingleton
import code.comet.status._
import code.comet.RegisteredListSingleton
import code.comet.ChatServer
import code.comet.MessageFromAdmin

class SwissCore extends RegistrationCore {

	val emptyPlayer = "Empty"
	val openGames = new GameSet()
	val playedGames = collection.mutable.LinkedHashMap[String, ListBuffer[Game]]()
	val scores = collection.mutable.LinkedHashMap[String, Int]()
	var totalRounds = 0
	var currentRound = 1

	def winPrice = 4 // currentRound match { case 1 => 2.0 case 2 => 2.4 case 3 => 2.8 case 4 => 3.2 case 5 => 3.6 case _ => 4.0 }
	def drawPrice = 2 // currentRound match { case 1 => 1.0 case 2 => 1.2 case 3 => 1.4 case 4 => 1.6 case 5 => 1.8 case _ => 2.0 }
	def lossPrice = 1 // currentRound match { case 1 => 0.5 case 2 => 0.6 case 3 => 0.7 case 4 => 0.8 case 5 => 0.9 case _ => 1.0 }

	def log2(x: Int) = (1 to 30).find(degree => (1 << degree) >= x).get

	// def receive: -- method in parent class

	// def registrationAssigned: -- method in parent class

	override def registrationInProgress =
		super.registrationInProgress.orElse {
			case StartTheTournament =>
				log.info("starting tournament")
				if (registered.size % 2 != 0) {
					// swiss tournament needs an even number of players
					register(PlayerInfo("Empty", "en", 1200, 0, 0, 0))
					//					registered += "Empty"
					//					scores += "Empty" -> 0
					//					RegisteredListSingleton ! "Empty"
					//					ChatServer ! MessageFromAdmin("Player Empty registered.")
					//					playedGames += "Empty" -> ListBuffer[Game]()
					//					totalRounds = log2(registered.size) + 2
				}
				startNewRound()
		}

	override def register(info: PlayerInfo) {
		if (registered.contains(info.nick)) {
			// already registered
		} else if (rankLimit > info.rank && info.nick != emptyPlayer) {
			toZagram ! MessageToZagram(info.nick+", sorry, rank limit is "+rankLimit+". Not registered.")
		} else if (info.nick startsWith "*") {
			toZagram ! MessageToZagram(info.nick+", to take a part in the tournament, please, use a registered account.")
		} else {
			if (importRankInSwiss && info.nick != emptyPlayer) {
				scores += info.nick -> info.rank / 100
			} else {
				scores += info.nick -> 0
			}
			registered += info.nick
			RegisteredListSingleton ! info.nick
			ChatServer ! MessageFromAdmin("Player "+info.nick+" registered.")
			playedGames += info.nick -> ListBuffer[Game]()
			totalRounds = log2(registered.size) + 2
			log.info("registered player: "+info.nick+". Total rounds now: "+totalRounds)
			notifyGui()
		}

	}

	def notifyGui() {
		val rows = scores.map { s =>
			val opponent = openGames.getOpponent(s._1)
			val opponentList = if (opponent.nonEmpty)
				playedGames(s._1).toList :+ Game(opponent.get, NotFinished)
			else playedGames(s._1).toList
			Player(s._1, opponentList, s._2)
		}
		val table = SwissTableData(totalRounds, rows.toList.sortBy(_.score).reverse)
		SwissTableSingleton ! table
	}

	def gamesInProgress: Receive = {
		case GameWon(winner, looser) =>
			if (openGames.contains(winner, looser)) {
				log.info("gameWon "+winner+" > "+looser)
				ChatServer ! MessageFromAdmin(winner+" won a game against "+looser)

				openGames -= (winner, looser)
				playedGames(winner) += Game(looser, Win)
				playedGames(looser) += Game(winner, Loss)

				scores.put(winner, scores(winner) + winPrice)
				scores.put(looser, scores(looser) + lossPrice)
				if (openGames.size == 0) tryWaitForNextRound()
				else notifyGui()
			}
		case GameDraw(first, second) =>
			if (openGames.contains(first, second)) {
				log.info("gameDraw "+first+" = "+second)
				ChatServer ! MessageFromAdmin("game "+first+" - "+second+" ended with draw")

				openGames -= (first, second)
				playedGames(first) += Game(second, Draw)
				playedGames(second) += Game(first, Draw)

				scores.put(first, scores(first) + drawPrice)
				scores.put(second, scores(second) + drawPrice)
				if (openGames.size == 0) tryWaitForNextRound()
				else notifyGui()
			}
		case RoundTimeout(round) =>
			if (round == currentRound) {
				if (sys.props.get("run.mode").getOrElse("") == "production") {
					openGames.toList.foreach(g => self ! GameDraw(g.a, g.b))
				} else {
					openGames.toList.foreach { g =>
						Random.nextInt(3) match {
							case 0 => self ! GameWon(g.a, g.b)
							case 1 => self ! GameWon(g.b, g.a)
							case _ => self ! GameDraw(g.a, g.b)
						}
					}
				}
			}
	}

	def tryWaitForNextRound() {
		if (openGames.size == 0) {
			if (currentRound + 1 > totalRounds) {
				log.info("tournament finished!")
				context.become(finished)
				val winners = scores.groupBy(_._2).toList.sortBy(_._1).last._2.toList.map(_._1)
				log.info("winners: " + winners)
				if (winners.size == 1)
					GlobalStatusSingleton ! FinishedWithWinner(winners(0))
				else
					GlobalStatusSingleton ! FinishedWithWinners(winners)
			} else {
				log.info("waiting for next round now")
				context.become(waitingForNextRound, discardOld = true)
				currentRound += 1
				GlobalStatusSingleton ! WaitingForNextTour(System.currentTimeMillis + tourBrakeTime)
				context.system.scheduler.scheduleOnce(tourBrakeTime milliseconds, self, StartNextRound)
			}
			notifyGui()
		}
	}

	def waitingForNextRound: Receive = {
		case StartNextRound => startNewRound()
	}

	def startNewRound() {
		if (openGames.size == 0) {
			val sortedPlayers = scores.toList.sortBy(s => (s._2, Random.nextInt)).reverse

			for (i <- 0.until(sortedPlayers.length, 2)) {
				val first = sortedPlayers(i)._1
				val second = sortedPlayers(i + 1)._1
				log.info("assigning game " + first + "-" + second)
				openGames +=(first, second)
				toZagram ! AssignGame(first, second)
			}
			context.become(gamesInProgress, discardOld = true)
			context.system.scheduler.scheduleOnce(gameTimeout milliseconds, self, RoundTimeout(currentRound))
			GlobalStatusSingleton ! GamePlaying(0)
			notifyGui()
		}
	}

	def finished: Receive = { case _ => }

}

private object StartNextRound

case class RoundTimeout(round: Int)

class GameSet {
	private val openGames = collection.mutable.LinkedHashSet[Opponents]()

	def +=(b: String, a: String) {
		openGames += (new Opponents(a, b))
	}
	def -=(b: String, a: String) {
		openGames -= (new Opponents(a, b))
	}
	def size = openGames.size
	def contains(a: String, b: String) = {
		openGames.contains(new Opponents(a, b))
	}
	def contains(p: String) = {
		openGames.exists(_.a == p) || openGames.exists(_.b == p)
	}
	def getOpponent(p: String): Option[String] = {
		openGames.find(_.a == p).map(_.b) orElse
			openGames.find(_.b == p).map(_.a)
	}
	def toList = openGames.toList
}

class Opponents(val a: String, val b: String) {
	override def equals(other: Any) = {
		val o = other.asInstanceOf[Opponents]
		(o.a == a && o.b == b) || (o.a == b && o.b == a)
		// that's a very bad definition of equals, I know...
	}
	override def hashCode = a.hashCode + b.hashCode
}
