package code.comet

object GameResultEnumeration extends Enumeration {
	type GameResult = Value
	val Draw, Win, Loss = Value
}

import GameResultEnumeration._

case class SwissTableData(val numberOfTours: Int, val rows: List[Row])

case class Row(val user: String, val games: List[Game])

case class Game(val opponent: String, val result: GameResult)

