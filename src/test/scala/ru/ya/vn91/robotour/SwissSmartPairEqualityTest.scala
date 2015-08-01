package ru.ya.vn91.robotour

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{FunSuite, Matchers}
import ru.ya.vn91.robotour.SwissPairMatcher._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class SwissSmartPairEqualityTest extends FunSuite with Matchers with TypeCheckedTripleEquals {

	val n = 10
	val roundsPlayed = 5
	def rand() = scala.util.Random.nextInt(n)
	def failAfter[T](time: FiniteDuration)(f: ⇒ T) = Await.result(Future(f), time)

	def compareSmartPairsWithDifferentStartingPermutation() = {
		failAfter(1.second) {
			val players = "abcdefghijklmnopqrstuvwxyz".take(n).map(_.toString)
			val scores = players.zip(Stream.continually(rand())).toMap
			var games = Map[String, List[String]]().withDefaultValue(Nil)
			for {
				p <- players
				_ <- 1 to roundsPlayed
				opponent = players(rand())
				if p < opponent
			} {
				games += p -> (opponent :: games(p))
				games += opponent -> (p :: games(opponent))
			}
			println("scores: " + scores.toList.sortBy(_._2))
			println("games: " + games)

			val pairs1 = makePairsSmart(scores, games)
			println("pairs1: " + pairs1.filter(pair ⇒ pair._1 < pair._2).toMap)

			// to resurrect this checking procedure:
			// refactor `makePairsSmart`, extract the initial `opponents` creation (it can be viewed as an input).
			// In the last 3 lines of this method compare outputs for different inputs.
			// They should not always be the same, but only complex reasonable permutations may be present.

			//			val pairs2 = makePairsSmart2(scores, games)
			//			println("pairs2: " + pairs2.filter(pair ⇒ pair._1 < pair._2).toMap)
			//			evaluate(scores, games, pairs1) shouldEqual evaluate(scores,games, pairs2)
		}
	}

	// Strictly speaking, this is not a test.
	// Outputs _can_ differ because the "smart" algorithm is not optimal.
	//
	//	test("same") {
	//		compareSmartPairsWithDifferentStartingPermutation()
	//	}

}
