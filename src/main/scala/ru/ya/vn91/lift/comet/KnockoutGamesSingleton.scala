package ru.ya.vn91.lift.comet

import net.liftweb.actor.LiftActor
import net.liftweb.http.ListenerManager
import ru.ya.vn91.robotour.GameNode

object KnockoutGamesSingleton extends LiftActor with ListenerManager {

	private var allGames = GameNode("")

	override def createUpdate = allGames

	override def lowPriority = {
		case s: GameNode =>
			allGames = s
			updateListeners()
	}
}
