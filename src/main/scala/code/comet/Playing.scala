package code.comet

import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import net.liftweb.util.ClearClearable
import ru.ya.vn91.robotour.GameNode
import scala.xml.NodeSeq

/** The screen real estate on the browser will be represented
	* by this component.  When the component changes on the server
	* the changes are automatically reflected in the browser.
	*/
class Playing extends CometActor with CometListener {
	private var playing = List[(GameNode, GameNode)]()

	def registerWith = PlayingSingleton

	override def lowPriority = {
		case v: List[_] =>
			playing = v.asInstanceOf[List[(GameNode, GameNode)]]
			reRender()
	}

	def gameToHtml(game: (GameNode, GameNode)) = {
		val duel = GameNode("???", game._1, game._2)
		<pre>{GameNode.toTree(duel)}</pre>: NodeSeq
	}

	def render = "li *+" #> playing.map(gameToHtml)

}
