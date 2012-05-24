package code.comet

import net.liftweb._
import http._
import actor._
import akka.actor._
import java.text.SimpleDateFormat
import java.util.TimeZone
import net.liftweb.util._
import Helpers._
import xml.{NodeSeq} // XML, Text

case class ChatMessage(val message: String, val time: Long = 0, val source: String = "", val sender: String = "")

/** A singleton that provides chat features to all clients.
 *  It's an Actor so it's thread-safe because only one
 *  message will be processed at once.
 */
object ChatServer extends LiftActor with ListenerManager {
	private var msgs = Vector[NodeSeq]() // private state

	/** When we update the listeners, what message do we send?
	 *  We send the msgs, which is an immutable data structure,
	 *  so it can be shared with lots of threads without any
	 *  danger or locking.
	 */
	def createUpdate = msgs

	//	val dateFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss zzzz")
	val dateFormatter = new SimpleDateFormat("HH:mm:ss")
	dateFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"))

	/** process messages that are sent to the Actor.  In
	 *  this case, we're looking for Strings that are sent
	 *  to the ChatServer.  We append them to our Vector of
	 *  messages, and then update all the listeners.
	 */
	override def lowPriority = {
		case ChatMessage(message, time, source, sender) =>
			val timeCorrect = (if (time > 0) time else System.currentTimeMillis)
			val line: NodeSeq = 
				NodeSeq.fromSeq(Seq(
					xml.Text(dateFormatter.format(new java.util.Date(timeCorrect))),
				<b>
					{ if (source != "") source+" " else "" }
					{ if (sender != "") sender+" " else "" }
				</b>,
				xml.Text(message)))
			msgs = msgs :+ line takeRight (60)
			updateListeners(msgs)
	}
}
