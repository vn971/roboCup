package code
package comet

import net.liftweb._
import http._
import util._
import Helpers._
import scala.collection.mutable.ListBuffer
import ru.ya.vn91.robotour.{ GameNode, Branch }
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
