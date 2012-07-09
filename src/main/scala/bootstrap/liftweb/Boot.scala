package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._
import common._
import http._
import sitemap._
import Loc._
import mapper._
import ru.ya.vn91.robotour.KnockoutCore
import java.util.Locale
import net.liftweb.http.provider.HTTPRequest
import net.liftweb.http.SessionVar
import net.liftweb.http.provider.HTTPCookie
import ru.ya.vn91.robotour.Core
import ru.ya.vn91.robotour.Constants._
import ru.ya.vn91.robotour.Utils

/** A class that's instantiated early and run.  It allows the application
 *  to modify lift's environment
 */
class Boot {
	def boot {
		if (!DB.jndiJdbcConnAvailable_?) {
			val vendor =
				new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
					Props.get("db.url") openOr
						"jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
					//						"jdbc:h2:tcp://localhost//data/data/dev/scala/roboCup/lift_proto.db",
					Props.get("db.user"), Props.get("db.password"))

			LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

			DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
		}

		// Use Lift's Mapper ORM to populate the database
		// you don't need to use Mapper to use Lift... use
		// any ORM you want
		//		Schemifier.schemify(true, Schemifier.infoF _, User)

		// where to search snippet
		LiftRules.addToPackages("code")

		//		def allFrance: Box[HTTPRequest] => Locale = _ =>
		//			java.util.Locale.FRANCE

		//		object definedLocale extends SessionVar[Box[Locale]](Empty)

		//		def customLocalizer: Box[HTTPRequest] => Locale = h => {
		//			val hbox = h.get
		////			println("httpReq: "+h)
		//			print("inn.loc: "+hbox.locale+"|")
		////			hbox.
		//			LiftRules.defaultLocaleCalculator(h)
		//			//			java.util.Locale.FRANCE
		//		}
		//
		//		LiftRules.localeCalculator = customLocalizer

		//		def localeCalculator(request: Box[HTTPRequest]): Locale = {
		//			//			println(S)
		//			//			println(request)
		//
		//			request.flatMap(r => {
		//				def localeCookie(in: String): HTTPCookie =
		//					HTTPCookie("cookie.locale", Full(in),
		//						Full(S.hostName), Full(S.contextPath), Full(2629743), Empty, Empty)
		//				def localeFromString(in: String): Locale = {
		//					val x = in.split("_").toList; new Locale(x.head, x.last)
		//				}
		//				def calcLocale: Box[Locale] =
		//					S.findCookie("cookie.locale").map(
		//						_.value.map(localeFromString)).openOr(Full(LiftRules.defaultLocaleCalculator(request)))
		//				//				println(S.param("locale"))
		//				S.param("locale") match {
		//					case Full(null) => calcLocale
		//					case f @ Full(selectedLocale) =>
		//						S.addCookie(localeCookie(selectedLocale))
		//						tryo(localeFromString(selectedLocale))
		//					case _ => calcLocale
		//				}
		//			}).openOr(Locale.getDefault())
		//		}
		//
		//		LiftRules.localeCalculator = localeCalculator

		//		LiftRules.localeCalculator = x =>{
		//			val default = LiftRules.defaultLocaleCalculator(x)
		//			if (default.getLanguage.contains("ua"))
		//				new java.util.Locale("ru")
		//			else
		//				default
		//		}

		//		val loc = Loc("HomePage", "index" :: Nil, "Home Page")
		//				val lp = LocParam

		val adminPage = {
			val adminPageAddr = sys.props.get("admin.page")
			if (adminPageAddr.nonEmpty)
				Menu.i("Administration").path(adminPageAddr.get) >> Hidden
			else
				Menu.i("Administration").path("admin")
		}

		def sitemap = SiteMap(
			Menu.i("Rules").path("index"),
			Menu.i("Registration").path("register"),
			Menu.i("Games").path(if (isKnockout) "knockout" else "swiss"),
			adminPage,
			Menu.i("Chat").path("chat"),
			Menu.i("Language").path("language") >> Hidden)

		//				LiftRules.setSiteMapFunc(() => User.sitemapMutator(sitemap))
		LiftRules.setSiteMap(sitemap)

		LiftRules.unloadHooks.append { () => println("roboCup actors shutdown"); Core.system.shutdown }

		Core // init the sigleton

		// Use jQuery 1.4
		LiftRules.jsArtifacts = net.liftweb.http.js.jquery.JQuery14Artifacts

		//Show the spinny image when an Ajax call starts
		LiftRules.ajaxStart =
			Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

		// Make the spinny image go away when it ends
		LiftRules.ajaxEnd =
			Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

		// Force the request to be UTF-8
		LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

		// What is the function to test if a user is logged in?
		//		LiftRules.loggedInTest = Full(() => User.loggedIn_?)

		// Make a transaction span the whole HTTP request
		S.addAround(DB.buildLoanWrapper)
	}
}
