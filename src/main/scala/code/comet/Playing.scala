package code.comet

import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import net.liftweb.util.ClearClearable
import ru.ya.vn91.robotour.Branch
import ru.ya.vn91.robotour.GameNode

/** The screen real estate on the browser will be represented
 *  by this component.  When the component changes on the server
 *  the changes are automatically reflected in the browser.
 */
class Playing extends CometActor with CometListener {
	private var playing = List[(GameNode, GameNode)]()

	def registerWith = PlayingSingleton

	override def lowPriority = {
		case v: List[(GameNode, GameNode)] => playing = v; reRender()
	}

	def gameToHtml(game: (GameNode, GameNode)) = {
		val question = Branch("???", game._1 :: game._2 :: Nil)
		xml.NodeSeq.fromSeq(Seq(
			<pre>{ question.toString }</pre>))
	}

	def render = {
		val nodeSeqList = playing.map(gameToHtml)
		"li *" #> nodeSeqList & ClearClearable
	}
}
