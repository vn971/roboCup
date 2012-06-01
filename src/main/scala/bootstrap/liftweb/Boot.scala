package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._
import common._
import http._
import sitemap._
import Loc._
import mapper._
import ru.ya.vn91.robotour.Core
import java.util.Locale
import net.liftweb.http.provider.HTTPRequest
import net.liftweb.http.SessionVar
import net.liftweb.http.provider.HTTPCookie

/** A class that's instantiated early and run.  It allows the application
 *  to modify lift's environment
 */
class Boot {
	def boot {
		//		if (!DB.jndiJdbcConnAvailable_?) {
		//			val vendor =
		//				new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
		//					Props.get("db.url") openOr
		//						"jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
		//					Props.get("db.user"), Props.get("db.password"))
		//
		//			LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)
		//
		//			DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
		//		}

		// Use Lift's Mapper ORM to populate the database
		// you don't need to use Mapper to use Lift... use
		// any ORM you want
		//    Schemifier.schemify(true, Schemifier.infoF _, User)

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

		// this is the administration page. It's address is being kept in secret. It's initialized from the system properties, individually for each project.
		//		val adminAddress = sys.props.get("adminMenu")
		//		val adminMenu =
		//			if (adminAddress.isEmpty)
		//				Menu.i("Administration") / "admin"
		//			else
		//				Menu("Administration") / adminAddress.get >> Hidden

		// Build SiteMap
		def sitemap = SiteMap(
			//						Menu(Loc("", Link(List("", "static", "index", "/"), false, ""), "Static Content")),
			Menu.i("Rules") / "index",
			Menu.i("Registration") / "register",
			Menu.i("Games") / "games",
			Menu.i("Administration") / "hidden145938" >> Hidden,
			Menu.i("Chat") / "chat",
			//			adminMenu,
			//			Menu.i("Test") / "test" >> Hidden,
			Menu.i("Language") / "language")

		// Menu.i("Home2") / "index" >> User.AddUserMenusAfter, // the simple way to declare a menu
		// // more complex because this menu allows anything in the
		// // /static path to be visible
		// Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
		//    "Static Content"))

		//		def sitemapMutators = User.sitemapMutator
		// set the sitemap.  Note if you don't want access control for
		// each page, just comment this line out.

		//		    LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))

		//		    LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))
		LiftRules.setSiteMap(sitemap)

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
		//    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

		// Make a transaction span the whole HTTP request
		//		S.addAround(DB.buildLoanWrapper)
	}
}
