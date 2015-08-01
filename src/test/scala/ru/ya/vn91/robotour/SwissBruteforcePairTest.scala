package ru.ya.vn91.robotour

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{FunSuite, Matchers}
import ru.ya.vn91.robotour.SwissPairMatcher._
import scala.collection.immutable.{HashMap, SortedSet}

class SwissBruteforcePairTest extends FunSuite with Matchers with TypeCheckedTripleEquals {

	test("0 players, 0 conflicts, 0 changes needed") {
		val players = SortedSet[String]()
		val games = HashMap("" -> Nil)
		makePairsBruteforceHelper(players, games) shouldEqual List()
	}

	test("2 players, 0 conflicts, 0 changes needed") {
		val players = SortedSet("a", "b")
		val games = HashMap("a" -> Nil, "b" -> Nil)
		makePairsBruteforceHelper(players, games) shouldEqual List("a" -> "b")
	}

	test("2 players, 1 conflict, 0 changes needed") {
		val players = SortedSet("a", "b")
		val games = HashMap("a" -> List("b"), "b" -> List("a"))
		makePairsBruteforceHelper(players, games) shouldEqual List("a" -> "b")
	}

	test("4 players, 0 conflicts, 0 changes needed") {
		val players = SortedSet("a", "b", "c", "d")
		val games = HashMap("a" -> Nil, "b" -> Nil, "c" -> Nil, "d" -> Nil)
		makePairsBruteforceHelper(players, games) shouldEqual List("a" -> "b", "c" -> "d")
	}

	test("4 players, 1 'bottom' conflict, no changes expected because of algorithm limitations") {
		val players = SortedSet("a", "b", "c", "d")
		val games = HashMap("a" -> Nil, "b" -> Nil, "c" -> List("d"), "d" -> List("c"))
		// the "bruteforce" method is stupid, it should not be able to optimize this
		makePairsBruteforceHelper(players, games) shouldEqual List("a" -> "b", "c" -> "d")
	}

	test("4 players, 1 conflict, 1 change needed") {
		val players = SortedSet("a", "b", "c", "d")
		val games = HashMap("a" -> List("b"), "b" -> List("a"), "c" -> Nil, "d" -> Nil)
		makePairsBruteforceHelper(players, games) shouldEqual List("a" -> "c", "b" -> "d")
	}

	test("4 players, 2 conflicts, 1 change needed") {
		val players = SortedSet("a", "b", "c", "d")
		val games = HashMap("a" -> List("b", "c"), "b" -> List("a"), "c" -> List("a"), "d" -> Nil)
		makePairsBruteforceHelper(players, games) shouldEqual List("a" -> "d", "b" -> "c")
	}

	test("4 players, 3 conflicts, 0 changes needed") {
		val players = SortedSet("a", "b", "c", "d")
		val games = HashMap("a" -> List("b", "c", "d"), "b" -> List("a"), "c" -> List("a"), "d" -> List("a"))
		makePairsBruteforceHelper(players, games) shouldEqual List("a" -> "b", "c" -> "d")
	}

}
