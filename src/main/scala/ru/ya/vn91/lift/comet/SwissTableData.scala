package ru.ya.vn91.lift.comet

object GameResultEnumeration extends Enumeration {
	type GameResult = Value
	val Win, Loss, Draw, NotFinished = Value
}

import ru.ya.vn91.lift.comet.GameResultEnumeration._

case class SwissTableData(numberOfTours: Int, rows: List[Player])

case class Player(name: String, games: List[Game], score: Int = 0)

case class Game(opponent: String, result: GameResult)

