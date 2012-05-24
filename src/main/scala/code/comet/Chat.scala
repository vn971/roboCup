package code.comet

import net.liftweb._
import http._
import util._
import Helpers._
import scala.xml.{ NodeSeq, Text }

//import code.lib._
//import Helpers._

/** The screen real estate on the browser will be represented
 *  by this component.  When the component changes on the server
 *  the changes are automatically reflected in the browser.
 */
class Chat extends CometActor with CometListener {
	private var msgs = Vector[NodeSeq]() // private state

	/** When the component is instantiated, register as
	 *  a listener with the ChatServer
	 */
	override def registerWith = ChatServer

	/** The CometActor is an Actor, so it processes messages.
	 *  In this case, we're listening for Vector[String],
	 *  and when we get one, update our private state
	 *  and reRender() the component.  reRender() will
	 *  cause changes to be sent to the browser.
	 */
	override def lowPriority = {
		case v: Vector[NodeSeq] => msgs = v.asInstanceOf[Vector[NodeSeq]]; reRender()
	}

	/** Put the messages in the li elements and clear
	 *  any elements that have the clearable class.
	 */
	def render = "li *" #> msgs & ClearClearable
}
