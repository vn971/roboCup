package ru.ya.vn91.lift.comet

import net.liftweb.http.{ CometActor, CometListener }
import ru.ya.vn91.robotour.GameNode
import scala.xml.NodeSeq

class KnockedOut extends CometActor with CometListener {
	private var knockedOut = List[GameNode]()

	def registerWith = KnockedOutSingleton

	override def lowPriority = {
		case l: List[_] => knockedOut = l.asInstanceOf[List[GameNode]]; reRender()
	}

	def render = {
		val nodeSeqList: List[NodeSeq] = knockedOut.map(node => <pre>{ node.toString }</pre>)
		"li *" #> nodeSeqList
	}

}
