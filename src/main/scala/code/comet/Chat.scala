package code.comet

import net.liftweb._
import http._
import util._
import Helpers._
import scala.xml.{ NodeSeq, Text }

class Chat extends CometActor with CometListener {
	private var msgs: Vector[NodeSeq] = Vector[NodeSeq]()

	override def registerWith = ChatServer

	override def lowPriority = {
		//		case n: NodeSeq =>
		//			if (msgs != null) {
		//				msgs :+ n
		//				// partialUpdate(?!)
		//				reRender()
		//			}
		case v: Vector[_] => if (msgs != null) {
			msgs = v.asInstanceOf[Vector[NodeSeq]]; reRender()
		}
	}

	def render = "li *" #> {
		//		if (msgs != null)
		msgs
		//		else ""
	}
	//		if (msgs != null) {
	//		"li *" #> msgs & ClearClearable
	//	} else {
	//		"" #> "" // "do nothing"
	//	}
}
