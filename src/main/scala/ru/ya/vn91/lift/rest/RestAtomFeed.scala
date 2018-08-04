package ru.ya.vn91.lift.rest

import ru.ya.vn91.lift.comet.MessageToChatServer
import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}
import net.liftweb.common.Loggable
import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.json._
import net.liftweb.util.BindHelpers._
import ru.ya.vn91.robotour.{Constants, Core}

object RestAtomFeed extends RestHelper with Loggable {

	def longToTimestamp(l: Long) = {
		val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
		formatter.format(new Date(l))
	}

	def debugText(req: Req): LiftResponse = {
		val headersJson = JObject(req.headers.map { case (k, v) => JField(k, JString(v)) })
		val response = JObject(
			JField("headers", headersJson) ::
				JField("time", JString(Constants.timeLongToString(System.currentTimeMillis))) ::
				Nil
		)
		response
	}

	serve {
		case Get("test" :: Nil, _) => PlainTextResponse("test confirmed")
		case Get("headers" :: Nil, req) => debugText(req)
		case Get("debug" :: Nil, req) => debugText(req)

		case Get("api" :: "chatFeed" :: Nil, _) =>

			val messages = Core.chatServer.msgs.takeRight(100).reverse

			def msgTransform(m: MessageToChatServer) = {
				val user = if (m.isAdmin) "server" else "user"
				"id *+" #> m.hashCode.toHexString &
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
