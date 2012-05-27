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

class HelloWorld {
	lazy val ran = math.random
	def random1 = "#ran *" #> ran.toString
	def random2 = "#ran *" #> ran.toString
	def timezone = "#zone *" #> ChatServer.dateFormatter.getTimeZone.getDisplayName

	def lang = {
		val cookie = try {
			S.findCookie("cookie.locale").get.value.get
		} catch {
			case e: Throwable => ""
		}

		val lang = "display="+locale.getDisplayLanguage(locale)+", inner="+locale.getLanguage+", cookie="+cookie

		//			xml.NodeSeq.fromSeq(Seq(
		//			Text(locale.getDisplayLanguage(locale)),
		//			<b> - </b>,
		//			Text(cookie),
		//			Text("")))

		//		val lang = xml.NodeSeq.fromSeq(Seq(
		//			Text(locale.getDisplayLanguage(locale)),
		//			<b> - </b>,
		//			Text(cookie),
		//			Text("")))
		//			"#lang" #> locale.getDisplayLanguage(locale) &
		"#lang" #> lang &
			"#select" #> SHtml.selectObj(locales.map(lo => (lo, lo.getDisplayName)), definedLocale, setLocale) &
			ClearClearable
	}

	private def locales =
		Locale.getAvailableLocales.toList.sortWith(_.getDisplayName < _.getDisplayName)

	private def setLocale(loc: Locale) = {
		//		println("Lang Set???! "+loc)
		S.addCookie(HTTPCookie("cookie.locale", Full(loc.toString),
			Full(S.hostName), Full(S.contextPath), Full(2629743), Empty, Empty))
		definedLocale(Full(loc))
	}
}

object definedLocale extends SessionVar[Box[Locale]](Empty)
