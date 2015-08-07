package code.comet

import net.liftweb.http._
import ru.ya.vn91.robotour.Constants._
import ru.ya.vn91.robotour.Core

class Chat extends CometActor with CometListener {
	private var msgs = Vector[MessageToChatServer]()

	override def registerWith = Core.chatServer

	override def lowPriority = {
		case v: Vector[_] =>
			msgs = v.asInstanceOf[Vector[MessageToChatServer]]
			reRender()
	}

	def render = "li *" #> msgs.map { m =>
		val user = if (m.isAdmin) "server" else "user"
		val color = if (m.isAdmin) "red" else "green"

		".time" #> timeLongToString(m.time) &
			".user [color]" #> color &
			".user *" #> user &
			".message" #> m.message
	}
}
