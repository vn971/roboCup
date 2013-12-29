package code.comet

import java.text.SimpleDateFormat
import java.util.TimeZone
import net.liftweb.actor._
import net.liftweb.common.Loggable
import net.liftweb.http._

case class MessageToChatServer(message: String, isAdmin: Boolean = true, time: Long = 0L)

object ChatServer extends LiftActor with ListenerManager with Loggable {

	private var _msgs = Vector[MessageToChatServer]()
	def msgs = _msgs

	def createUpdate = msgs

	val dateFormatter = new SimpleDateFormat("HH:mm:ss")
	dateFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"))

	override def lowPriority = {
		case m: MessageToChatServer =>
			_msgs = _msgs :+ m.copy(time = System.currentTimeMillis) takeRight 80
			updateListeners(msgs)
	}
}
