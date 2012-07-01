package code.comet

object GameResultEnumeration extends Enumeration {
	type GameResult = Value
	val Win, Loss, Draw, NotFinished = Value
}

import GameResultEnumeration._

case class SwissTableData(val numberOfTours: Int, val rows: List[Player])

case class Player(val name: String, val games: List[Game], val score: Double = 0.0)

case class Game(val opponent: String, val result: GameResult)

