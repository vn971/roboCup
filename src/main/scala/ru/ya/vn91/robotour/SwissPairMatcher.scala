package ru.ya.vn91.robotour

import net.liftweb.common.Loggable
import scala.collection.immutable._
import scala.util.Random

object SwissPairMatcher extends Loggable {

	private[robotour] def evaluate(
		scores: Map[String, Int],
		games: Map[String, List[String]],
		opponents: Map[String, String]): Int
	= {
		def diff(x: Int, y: Int) = (x - y) * (x - y)
		def meetings(x: String, y: String) = games(x).count(_ == y)
		(for {
			a <- scores.keySet
			vsA = opponents(a)
			if a < vsA
		} yield {
			diff(scores(a), scores(vsA)) + meetings(a, vsA)
		}).sum
	}

	def makePairsSmart(scores: Map[String, Int], games: Map[String, List[String]]): HashMap[String, String] = {

		var opponents = HashMap[String, String]()

		// initial pairs
		//players.indices.foreach { i ⇒
		//	opponents += players(i) -> players(players.size - 1 - i)
		//}
		val players = scores.toList.sortBy(_._2 -> Random.nextInt()).map(_._1).toVector.reverse
		for (i <- 0.until(players.length, 2)) {
			val first = players(i)
			val second = players(i + 1)
			opponents += first -> second
			opponents += second -> first
		}

		def diff(x: Int, y: Int) = (x - y) * (x - y)
		def meetings(x: String, y: String) = games(x).count(_ == y)

		val mutateAndCount = Stream.continually {
			var bestScore = 0
			var bestPlayerA = ""
			var bestPlayerB = ""
			for {
				a <- players
				vsA = opponents(a)
				b <- players
				if b != a
				if b != vsA
				vsB = opponents(b)
			} {
				val previousScoreDiff =
					diff(scores(a), scores(vsA)) +
						diff(scores(b), scores(vsB))
				val newScoreDiff =
					diff(scores(a), scores(b)) +
						diff(scores(vsA), scores(vsB))
				val meetingsScore =
					meetings(a, vsA) + meetings(b, vsB) -
						meetings(a, b) - meetings(vsA, vsB)
				val totalScore = meetingsScore * 1000 + previousScoreDiff - newScoreDiff // constant 1000 is a hack
				if (totalScore > bestScore) {
					bestScore = totalScore
					bestPlayerA = a
					bestPlayerB = b
				}
			}
			if (bestScore > 0) {
				val a = bestPlayerA
				val b = bestPlayerB
				val vsA = opponents(a)
				val vsB = opponents(b)
				logger.trace(s"transposing: $a-$vsA $b-$vsB")
				opponents += a -> b
				opponents += b -> a
				opponents += vsA -> vsB
				opponents += vsB -> vsA
			}
			bestScore
		}.takeWhile(_ > 0).size
		logger.debug(s"pairs calculated after $mutateAndCount iterations")
		opponents
	}

}
