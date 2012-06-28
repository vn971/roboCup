package code.comet

import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import net.liftweb.util.IterableConst.itNodeSeq
import ru.ya.vn91.robotour.GameNode
import scala.xml.NodeSeq

class KnockedOut extends CometActor with CometListener {
	private var knockedOut = List[GameNode]()

	def registerWith = KnockedOutSingleton

	override def lowPriority = {
		case l: List[_] => knockedOut = l.asInstanceOf[List[GameNode]]; reRender()
	}

	def render = {
		val nodeSeqList : List[NodeSeq] = knockedOut.map(node => <pre>{ node.toString }</pre>)
		"li *" #> nodeSeqList
	}

}
