package code.snippet

import net.liftweb._
import http._
import js._
import JsCmds._
import JE._
import code.comet.ChatServer
import net.liftweb.common.Loggable
import code.comet.MessageFromGuest

object ChatIn extends Loggable {

	def render = SHtml.onSubmit(s => {
		logger.info("chat received: "+s)
		if (!s.matches("\\s*")) {
			ChatServer ! MessageFromGuest(s.take(140))
		}
		SetValById("chat_in", "")
	})

}

