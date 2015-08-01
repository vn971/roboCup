package ru.ya.vn91.robotour

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{FunSuite, Matchers}
import ru.ya.vn91.robotour.SwissPairMatcher._
import scala.collection.immutable._

class SwissSmartPairTest extends FunSuite with Matchers with TypeCheckedTripleEquals {

	def pairTester(scores: Map[String, Int], games: Map[String, List[String]]) =
		makePairsSmart(scores, games)
			.toList.filter(pair ⇒ pair._1 < pair._2)

	test("0 players, 0 conflicts, 0 changes needed") {
		val players = Map[String, Int]()
		val games = HashMap("" -> Nil)
		pairTester(players, games) shouldEqual List()
	}

	test("2 players, 0 conflicts, 0 changes needed") {
		val players = Map("a" -> 2, "b" -> 1)
		val games = HashMap("a" -> Nil, "b" -> Nil)
		pairTester(players, games) shouldEqual List("a" -> "b")
	}

	test("2 players, 1 conflict, 0 changes needed") {
		val players = Map("a" -> 2, "b" -> 1)
		val games = HashMap("a" -> List("b"), "b" -> List("a"))
		pairTester(players, games) shouldEqual List("a" -> "b")
	}

	test("4 players, 0 conflicts, 0 changes needed") {
		val players = Map("a" -> 4, "b" -> 3, "c" -> 2, "d" -> 1)
		val games = HashMap("a" -> Nil, "b" -> Nil, "c" -> Nil, "d" -> Nil)
		pairTester(players, games) shouldEqual List("a" -> "b", "c" -> "d")
	}

	test("4 players unsorted, 0 conflicts, 0 changes needed") {
		val players = Map("a" -> 4, "b" -> 1, "c" -> 2, "d" -> 3)
		val games = HashMap("a" -> Nil, "b" -> Nil, "c" -> Nil, "d" -> Nil)
		pairTester(players, games) shouldEqual List("a" -> "d", "b" -> "c")
	}

	test("4 players, 1 conflict, 1 change needed") {
		val players = Map("a" -> 4, "b" -> 3, "c" -> 2, "d" -> 1)
		val games = HashMap("a" -> List("b"), "b" -> List("a"), "c" -> Nil, "d" -> Nil)
		pairTester(players, games) shouldEqual List("a" -> "c", "b" -> "d")
	}

	test("4 players, 5 conflicts, 0 changes needed") {
		val players = Map("a" -> 4, "b" -> 3, "c" -> 2, "d" -> 1)
		val games = HashMap("a" -> List("b"), "b" -> List("a", "c", "c", "d", "d"), "c" -> List("b", "b"), "d" -> List("b", "b"))
		pairTester(players, games) shouldEqual List("a" -> "b", "c" -> "d")
	}

	test("4 players, 1 'bottom' conflict, 2 changes needed") {
		val players = Map("a" -> 4, "b" -> 3, "c" -> 2, "d" -> 1)
		val games = HashMap("a" -> Nil, "b" -> Nil, "c" -> List("d"), "d" -> List("c"))
		pairTester(players, games) shouldEqual List("a" -> "c", "b" -> "d")
	}

	test("4 players, 2 conflicts, 1 change needed") {
		val players = Map("a" -> 4, "b" -> 3, "c" -> 2, "d" -> 1)
		val games = HashMap("a" -> List("b", "c"), "b" -> List("a"), "c" -> List("a"), "d" -> Nil)
		pairTester(players, games) shouldEqual List("a" -> "d", "b" -> "c")
	}

	test("4 players, 3 conflicts, 0 changes needed") {
		val players = Map("a" -> 4, "b" -> 3, "c" -> 2, "d" -> 1)
		val games = HashMap("a" -> List("b", "c", "d"), "b" -> List("a"), "c" -> List("a"), "d" -> List("a"))
		pairTester(players, games) shouldEqual List("a" -> "b", "c" -> "d")
	}

}
