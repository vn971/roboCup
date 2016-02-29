package ru.ya.vn91.lift.comet

import net.liftweb.http.{ CometActor, CometListener }
import ru.ya.vn91.robotour.GameNode

class KnockoutGames extends CometActor with CometListener {

	private var allGames = GameNode("")

	def registerWith = KnockoutGamesSingleton

	override def lowPriority = {
		case v: GameNode =>
			allGames = v
			reRender()
	}

	def render = {
		val xml = <pre>{ GameNode.toTree(allGames) }</pre>
		"*" #> xml
	}

}
