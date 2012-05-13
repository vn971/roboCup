package code
package comet

import net.liftweb._
import http._
import util._
import Helpers._
import scala.collection.mutable.ListBuffer
import ru.ya.vn91.robotour.{ GameNode, Branch }

/** The screen real estate on the browser will be represented
 *  by this component.  When the component changes on the server
 *  the changes are automatically reflected in the browser.
 */
class KnockedOut extends CometActor with CometListener {
	private var knockedOut = List[GameNode]()

	/** When the component is instantiated, register as
	 *  a listener with the ChatServer
	 */
	def registerWith = KnockedOutSingleton

	/** The CometActor is an Actor, so it processes messages.
	 *  In this case, we're listening for Vector[String],
	 *  and when we get one, update our private state
	 *  and reRender() the component.  reRender() will
	 *  cause changes to be sent to the browser.
	 */
	override def lowPriority = {
		case l: List[GameNode] => knockedOut = l; reRender()
	}

	/** Put the messages in the li elements and clear
	 *  any elements that have the clearable class.
	 */
	def render = "li *" #> knockedOut.map(node => <pre>{ node.toString }</pre>) & ClearClearable
}
