package code
package snippet

import net.liftweb._
import http._
import js._
import JsCmds._
import JE._
import comet.ChatServer
import code.comet.ChatMessage
import java.text.SimpleDateFormat
import java.util.TimeZone
import ru.ya.vn91.robotour.Core
import ru.ya.vn91.robotour.StartRegistration
import xml.NodeSeq

object BTT {

	//	def render = SHtml.hidden(S=>Unit, null)

	//	def render = SHtml.onSubmitUnit { () => println("OLOLO") }

	//	def render = SHtml.onSubmit(s => {
	//		println("OLOLO "+s)
	//		ChatServer ! ChatMessage(0L, "test", "", "OLOLO!! "+s)
	//	})

	//	def render = SHtml.onEvent {
	//		s =>
	//			println(s)
	//			def func(x: NodeSeq) = xml.NodeSeq.fromSeq(Seq())
	//		//			def func(x: xml.NodeSeq): xml.NodeSeq = (x : xml.NodeSeq => x)
	//	}
}