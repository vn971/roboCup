package ru.ya.vn91.lift.comet

import net.liftweb.actor.LiftActor
import net.liftweb.http.ListenerManager

object SwissTableSingleton extends LiftActor with ListenerManager {

	private var table = SwissTableData(0, List[Player]())
	// table = SwissTableData(3, Player("Вася Новиков", Game("Фрося", Loss) :: Nil) :: Player("Фрося", Game("Вася Новиков", NotFinished) :: Nil) :: Nil)

	def createUpdate = table

	override def lowPriority = {
		case t: SwissTableData =>
			table = t
			updateListeners()
		case s =>
			System.err.println("received unknown message: " + s)
	}
}
