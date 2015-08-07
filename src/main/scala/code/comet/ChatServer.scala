package code.comet

import net.liftweb.actor._
import net.liftweb.common.Loggable
import net.liftweb.http._

case class MessageToChatServer(message: String, isAdmin: Boolean = true, time: Long = 0L)

class ChatServer extends LiftActor with ListenerManager with Loggable {

	private var _msgs = Vector[MessageToChatServer]()
	def msgs = _msgs

	def createUpdate = msgs

	override def lowPriority = {
		case m: MessageToChatServer =>
			_msgs = _msgs :+ m.copy(time = System.currentTimeMillis) takeRight 80
			sendListenersMessage(msgs)
	}
}
