package code
package snippet

import scala.xml.{ NodeSeq, Text }
import net.liftweb.util._
import net.liftweb.common._
import java.util.Date
import code.lib._
import Helpers._
import code.comet.ChatServer
import java.util.Locale
import net.liftweb.http.SessionVar
import net.liftweb.mapper._
import net.liftweb.http.S
import net.liftweb.http.S._
import net.liftweb.http.SHtml
import net.liftweb.http.provider.HTTPRequest
import net.liftweb.http.LiftRules
import net.liftweb.http.provider.HTTPCookie
import ru.ya.vn91.robotour.Utils
import net.liftweb.http.js.JsCmds.SetValById

class ZagramUserinfo {

	def render = {
		//		val img = <img src={ Text("http://test.img.to") }/>
		val polishDajPref = Utils.getLinkContent("http://zagram.org/auth.py?co=dajPref&opisGracza=Вася%20Новиков")
		val linkText = Utils.getLinkContent("http://zagram.org/auth.py?co=dajOpis&opisGracza=Вася%20Новиков")
		val isOk = polishDajPref startsWith "ok."
		"img" #> {
			if (isOk)
				<img src={ Text("http://zagram.org/awatary/"+polishDajPref.split("\\.")(2)+".gif") }/>
			else
				Text("user not found")
		} & {
			ClearClearable
		}
	}
}

object ZagramUserInput {
	def render = SHtml.onSubmit(s => {
		//		if (!s.matches("\\s*")) ChatServer ! ChatMessage(s.take(140), 0, "local", "")
		SetValById("chat_in", "")
	})
}