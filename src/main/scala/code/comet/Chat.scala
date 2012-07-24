package code.comet

import net.liftweb._
import http._
import util._
import Helpers._
import scala.xml.{ NodeSeq, Text }
import ru.ya.vn91.robotour.Constants._

class Chat extends CometActor with CometListener {
	private var msgs: Vector[(MessageToChatServer, Long)] = Vector[(MessageToChatServer, Long)]()

	override def registerWith = ChatServer

	override def lowPriority = {
		case v: Vector[_] =>
			msgs = v.asInstanceOf[Vector[(MessageToChatServer, Long)]]; reRender()
	}

	def render = "li *" #> {
		//		println("render...")
		//		println("msgs.size="+msgs.size)
		msgs.map {
			case (MessageFromGuest(message), t: Long) =>
				val line: NodeSeq =
					NodeSeq.fromSeq(Seq(
						xml.Text(timeLongToString(t)),
						<b> <font color="green">{ "local" } </font></b>,
						xml.Text(message)))
				line
			case (MessageFromAdmin(message), t: Long) =>
				""
				val line: NodeSeq =
					NodeSeq.fromSeq(Seq(
						xml.Text(timeLongToString(t)),
						<b> <font color="red">{ "serv" } </font></b>,
						xml.Text(message)))
				line
			case _ => NodeSeq.Empty
		}
	}
}
