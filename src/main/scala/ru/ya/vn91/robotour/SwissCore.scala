package ru.ya.vn91.robotour

import code.comet.GameResultEnumeration._
import code.comet.TournamentStatus._
import code.comet.{ Game, Player, SwissTableData, _ }
import net.liftweb.util.Props
import ru.ya.vn91.robotour.Constants._
import ru.ya.vn91.robotour.Utils.SuppressWartRemover
import ru.ya.vn91.robotour.zagram._
import scala.collection.immutable.{ HashMap, HashSet }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

class SwissCore extends RegistrationCore {

	val emptyPlayer = "Empty"
	var openGames = new GameSet()
	var playedGames = HashMap[String, List[Game]]()
	var scores = HashMap[String, Int]()
	var totalRounds = 0
	var currentRound = 1

	def winPrice = 4 // currentRound match { case 1 => 2.0 case 2 => 2.4 case 3 => 2.8 case 4 => 3.2 case 5 => 3.6 case _ => 4.0 }
	def drawPrice = 2 // currentRound match { case 1 => 1.0 case 2 => 1.2 case 3 => 1.4 case 4 => 1.6 case 5 => 1.8 case _ => 2.0 }
	def lossPrice = 1 // currentRound match { case 1 => 0.5 case 2 => 0.6 case 3 => 0.7 case 4 => 0.8 case 5 => 0.9 case _ => 1.0 }

	def log2(x: Int) = (1 to 30).find(degree => (1 << degree) >= x).getOrElse(sys.error(""))

	override def registrationInProgress =
		super.registrationInProgress.orElse {
			case StartTheTournament =>
				logger.info(s"starting tournament. Number of registered players: ${registered.size}")
				if (registered.size % 2 != 0) {
					// swiss tournament needs an even number of players
					tryRegister(PlayerInfo(emptyPlayer, 1200, 0, 0, 0))
				}
				startNewRound()
		}

	override def tryRegister(p: PlayerInfo): Unit = {
		if (registered.contains(p.nick)) {
			// already registered
		} else if (rankLimit.exists(_ > p.rank) && p.nick != emptyPlayer) {
			Core.toZagramActor ! MessageToZagram(s"${p.nick}, sorry, rank limit is ${rankLimit.openOr(0)}. Not registered.")
		} else {
			logger.info(s"registered ${p.nick}")
			registered += p.nick
			if (importRankInSwiss && p.nick != emptyPlayer) {
				scores += p.nick -> p.rank / 100
			} else {
				scores += p.nick -> 0
			}
			RegisteredListSingleton ! p.nick
			ChatServer ! MessageToChatServer(s"Player ${p.nick} registered.")
			playedGames += p.nick -> List[Game]()
			totalRounds = log2(registered.size) + 2
			logger.info(s"registered player: ${p.nick}. Total rounds now: $totalRounds")
			notifyGui()
		}
	}

	def notifyGui(): Unit = {
		val rows = scores.map { s =>
			val currentOpponent = openGames.getOpponent(s._1)
			val currentGame = currentOpponent.map(o => Game(o, NotFinished))
			val games = playedGames(s._1) ++ currentGame
			Player(s._1, games, s._2)
		}
		val table = SwissTableData(totalRounds, rows.toList.sortBy(_.score).reverse)
		SwissTableSingleton ! table
	}

	def gamesInProgress: Receive = {
		case GameWon(winner, looser) =>
			if (openGames.contains(winner, looser)) {
				logger.info(s"gameWon $winner > $looser")
				ChatServer ! MessageToChatServer(s"$winner won a game against $looser")

				openGames -= (winner, looser)
				playedGames += winner -> (playedGames(winner) :+ Game(looser, Win))
				playedGames += looser -> (playedGames(looser) :+ Game(winner, Loss))

				scores += winner -> (scores(winner) + winPrice)
				scores += looser -> (scores(looser) + lossPrice)
				if (openGames.size == 0) tryWaitForNextRound()
				else notifyGui()
			}
		case GameDraw(first, second) =>
			if (openGames.contains(first, second)) {
				logger.info(s"gameDraw $first = $second")
				ChatServer ! MessageToChatServer(s"game $first - $second ended with draw")

				openGames -= (first, second)
				playedGames += first -> (playedGames(first) :+ Game(second, Draw))
				playedGames += second -> (playedGames(second) :+ Game(first, Draw))

				scores += first -> (scores(first) + drawPrice)
				scores += second -> (scores(second) + drawPrice)
				if (openGames.size == 0) tryWaitForNextRound()
				else notifyGui()
			}
		case RoundTimeout(round) =>
			if (round == currentRound) {
				if (Props.devMode) {
					openGames.toList.foreach { g =>
						Random.nextInt(3) match {
							case 0 => self ! GameWon(g.a, g.b)
							case 1 => self ! GameWon(g.b, g.a)
							case _ => self ! GameDraw(g.a, g.b)
						}
					}
				} else {
					openGames.toList.foreach(g => self ! GameDraw(g.a, g.b))
				}
			}
	}

	protected def tryWaitForNextRound(): Unit = {
		if (openGames.size == 0) {
			if (currentRound + 1 > totalRounds) {
				logger.info("tournament finished!")
				context.become(finished)
				val winners = scores.keys.groupBy(scores).toList.sortBy(_._1).lastOption.toList.flatMap(_._2)
				logger.info(s"winners: $winners")
				if (winners.size == 1) {
					GlobalStatusSingleton ! FinishedWithWinner(winners(0))
				} else {
					GlobalStatusSingleton ! FinishedWithWinners(winners)
				}
			} else {
				logger.info("waiting for next round now")
				context.become(waitingForNextRound, discardOld = true)
				currentRound += 1
				GlobalStatusSingleton ! WaitingForNextTour(System.currentTimeMillis + breakTime.toMillis)
				context.system.scheduler.scheduleOnce(breakTime, self, StartNextRound)
			}.suppressWartRemover()
			notifyGui()
		}
	}

	def waitingForNextRound: Receive = {
		case StartNextRound => startNewRound()
	}

	def startNewRound(): Unit = {
		if (openGames.size == 0) {
			val sortedPlayers = scores.toList.sortBy(s => (s._2, Random.nextInt())).reverse

			for (i <- 0.until(sortedPlayers.length, 2)) {
				val first = sortedPlayers(i)._1
				val second = sortedPlayers(i + 1)._1

				openGames += (first, second)
				if (first == emptyPlayer) {
					logger.info(s"$second assigned as winner against $emptyPlayer")
					self ! GameWon(second, emptyPlayer)
				} else if (second == emptyPlayer) {
					logger.info(s"$first assigned as winner against $emptyPlayer")
					self ! GameWon(first, emptyPlayer)
				} else {
					logger.info(s"assigning game $first-$second")
					Core.toZagramActor ! AssignGame(first, second)
				}
			}

			context.become(gamesInProgress, discardOld = true)
			context.system.scheduler.scheduleOnce(gameTimeout, self, RoundTimeout(currentRound)).suppressWartRemover()
			GlobalStatusSingleton ! GamePlaying(0)
			notifyGui()
		}
	}

	def finished: Receive = { case _ => }

}

private object StartNextRound

case class RoundTimeout(round: Int)

class GameSet(openGames: HashSet[Opponents] = HashSet.empty) {

	def +(b: String, a: String) = new GameSet(openGames + new Opponents(a, b))

	def -(b: String, a: String) = new GameSet(openGames - new Opponents(a, b))

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
	override def equals(otherAny: Any) = otherAny match {
		case otherOp: Opponents =>
			(otherOp.a == a && otherOp.b == b) || (otherOp.a == b && otherOp.b == a)
		case _ => false
	}
	override def hashCode = a.hashCode + b.hashCode
}
