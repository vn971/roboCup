package code.comet

import net.liftweb._
import http._
import actor._
import akka.actor._
import java.text.SimpleDateFormat
import java.util.TimeZone
import net.liftweb.util._
import Helpers._
import xml.{ NodeSeq } // XML, Text
import net.liftweb.common.Logger

sealed class MessageToChatServer
//case class ChatMessage(val message: String, val time: Long = 0, val source: String = "", val sender: String = "") extends MessageToChatServer
case class MessageFromGuest(val message: String) extends MessageToChatServer
case class MessageFromAdmin(val message: String) extends MessageToChatServer

object ChatServer extends LiftActor with ListenerManager with Logger {

	private var msgs = Vector[NodeSeq]() // private state

	def createUpdate = msgs

	//	val dateFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss zzzz")
	val dateFormatter = new SimpleDateFormat("HH:mm:ss")
	dateFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"))

	override def lowPriority = {
		//		case ChatMessage(message, time, source, sender) =>
		//			val timeCorrect = (if (time > 0) time else System.currentTimeMillis)
		//			val line: NodeSeq =
		//				NodeSeq.fromSeq(Seq(
		//					xml.Text(dateFormatter.format(new java.util.Date(timeCorrect))),
		//					<b>
		//						{ if (source != "") source+" " else "" }
		//						{ if (sender != "") sender+" " else "" }
		//					</b>,
		//					xml.Text(message)))
		//			msgs = msgs :+ line takeRight (60)
		//			updateListeners(msgs)

		case MessageFromGuest(message) =>
			val line: NodeSeq =
				NodeSeq.fromSeq(Seq(
					xml.Text(dateFormatter.format(new java.util.Date())),
					<b> <font color="green">{ "local" } </font></b>,
					xml.Text(message)))
			msgs = msgs :+ line takeRight (80)
			updateListeners(msgs)

		case MessageFromAdmin(message) =>
			val line: NodeSeq =
				NodeSeq.fromSeq(Seq(
					xml.Text(dateFormatter.format(new java.util.Date())),
					<b> <font color="red">{ "serv" } </font></b>,
					xml.Text(message)))
			msgs = msgs :+ line takeRight (80)
			updateListeners(msgs)
	}
}
