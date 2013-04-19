package code.comet

import java.text.SimpleDateFormat
import java.util.TimeZone
import net.liftweb.actor._
import net.liftweb.common.Loggable
import net.liftweb.http._

sealed class MessageToChatServer
case class MessageFromGuest(message: String) extends MessageToChatServer
case class MessageFromAdmin(message: String) extends MessageToChatServer

object ChatServer extends LiftActor with ListenerManager with Loggable {

	private var msgs = Vector[(MessageToChatServer, Long)]() // private state

	def createUpdate = msgs

	val dateFormatter = new SimpleDateFormat("HH:mm:ss")
	dateFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"))

	override def lowPriority = {
		case m: MessageToChatServer =>
			msgs = msgs :+ (m, System.currentTimeMillis) takeRight (80)
			updateListeners(msgs)
		//		case MessageFromGuest(message) =>
		//			val line: NodeSeq =
		//				NodeSeq.fromSeq(Seq(
		//					xml.Text(dateFormatter.format(new java.util.Date())),
		//					<b> <font color="green">{ "local" } </font></b>,
		//					xml.Text(message)))
		//			msgs = msgs :+ line takeRight (80)
		//			updateListeners(msgs)

		//		case MessageFromAdmin(message) =>
		//			val line: NodeSeq =
		//				NodeSeq.fromSeq(Seq(
		//					xml.Text(dateFormatter.format(new java.util.Date())),
		//					<b> <font color="red">{ "serv" } </font></b>,
		//					xml.Text(message)))
		//			msgs = msgs :+ line takeRight (80)
		//			updateListeners(msgs)
	}
}
