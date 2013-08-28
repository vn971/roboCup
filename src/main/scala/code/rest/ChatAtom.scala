package code.rest

import code.comet.{MessageToChatServer, ChatServer}
import java.text.SimpleDateFormat
import java.util.{TimeZone, Date}
import net.liftweb.common.Loggable
import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.util.BindHelpers._

object ChatAtom extends RestHelper with Loggable {

	def longToTimestamp(l: Long) = {
		val frmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
		frmt.setTimeZone(TimeZone.getTimeZone("UTC"))
		frmt.format(new Date(l))
	}

	serve {
		case Get("api" :: "chatFeed" :: Nil, _) =>

			val messages = ChatServer.msgs

			def msgTransform(m: MessageToChatServer) = {
				val user = if (m.isAdmin) "serv" else "local"
				"id *+" #> m.hashCode &
						"title *" #> m.message.take(100) &
						"updated *" #> longToTimestamp(m.time) &
						"content span *" #> m.message.take(1000) &
						"author name *" #> user
			}

			val transform =
				"entry *" #> messages.map(msgTransform) &
						"link [href]" #> (S.hostAndPath + "/chat") &
						"updated" #> messages.headOption.map {
							lastM => "* *" #> longToTimestamp(lastM.time)
						}

			for {
				xml <- Templates("templates-hidden" :: "chat2atom" :: Nil)
				feed <- transform(xml).headOption
			} yield AtomResponse(feed)

	}


}
