package code
package snippet

import net.liftweb._
import http._
import js._
import JsCmds._
import JE._
import comet.ChatServer
import net.liftweb.common.Logger
import code.comet.MessageFromGuest

object ChatIn {

	val log = Logger(ChatIn.getClass)

	def render = SHtml.onSubmit(s => {
		log.info("chat received: "+s)
		if (!s.matches("\\s*")) {
			ChatServer ! MessageFromGuest(s.take(140))
		}
		SetValById("chat_in", "")
	})

}

