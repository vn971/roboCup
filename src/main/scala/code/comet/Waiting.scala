package code.comet

import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import net.liftweb.util.ClearClearable
import ru.ya.vn91.robotour.GameNode

/** The screen real estate on the browser will be represented
 *  by this component.  When the component changes on the server
 *  the changes are automatically reflected in the browser.
 */
class Waiting extends CometActor with CometListener {

	private var waiting = List[GameNode]()

	def registerWith = WaitingSingleton

	override def lowPriority = {
		case v: List[_] => waiting = v.asInstanceOf[List[GameNode]]; reRender()
	}

	def render = {
		val nodeSeqList : List[xml.NodeSeq] = waiting.map(node => <pre>{ GameNode.toTree(node) }</pre>)
		"li *+" #> nodeSeqList
	}
}
