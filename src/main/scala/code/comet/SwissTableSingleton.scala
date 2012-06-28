package code.comet

import net.liftweb.actor.LiftActor
import net.liftweb.http.ListenerManager
import GameResultEnumeration._

object SwissTableSingleton extends LiftActor with ListenerManager {
//	private var table = SwissTableData(0, List[Row]())
	private var table = SwissTableData(3, Row("Вася Новиков", Game("Фрося", Loss) :: Nil) :: Row("Фрося", Game("Вася Новиков", Win) :: Nil) :: Nil)

	def createUpdate = table

	override def lowPriority = {
		case t: SwissTableData =>
			table = t
			updateListeners()
		case s =>
			System.err.println("received unknown message: "+s)
	}
}
