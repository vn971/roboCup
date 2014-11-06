package code.snippet

import code.comet.{ChatServer, MessageToChatServer}
import net.liftweb.common.Loggable
import net.liftweb.http._
import net.liftweb.http.js.JsCmds._

object ChatIn extends Loggable {

	def render = SHtml.onSubmit(s => {
		logger.info("chat received: "+s)
		if (!s.matches("\\s*")) {
			ChatServer ! MessageToChatServer(s.take(140), isAdmin = false)
		}
		SetValById("chat_in", "")
	})

}

