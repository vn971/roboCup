package bootstrap.liftweb

import java.util.Locale
import net.liftmodules.JQueryModule
import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.http.js.jquery.JQueryArtifacts
import net.liftweb.http.provider.HTTPCookie
import net.liftweb.sitemap.Loc._
import net.liftweb.sitemap._
import net.liftweb.util.Props
import ru.ya.vn91.robotour.Constants._
import ru.ya.vn91.robotour.{Constants, Core}
import scala.concurrent.duration._
import code.rest.ChatAtom


/** A class that's instantiated early and run.  It allows the application
 *  to modify lift's environment
 */
class Boot extends Loggable {
	def boot() {

		logger.info("props will be taken from: " + Props.toTry.map(_()))

		// where to search snippet
		LiftRules.addToPackages("code")

		LiftRules.statelessDispatch.append(ChatAtom)

		val adminPage =
			Constants.adminPage.map(
				Menu.i("Админка").path(_) >> Hidden >>
						TemplateBox(() => Templates("admin" :: Nil))
			).getOrElse(
				Menu.i("Админка").path("admin")
			)

		def sitemap = SiteMap(
			Menu.i("Main").path("index"),
			Menu.i("Registration").path("register"),
			Menu.i(tournamentCodename).path(if (isSwiss) "swiss" else "knockout"),
			adminPage,
			Menu.i("Chat").path("chat"),
			Menu.i("Pointsgame League 2013").path("pl2013"),
			Menu.i("Language").path("language") >> Hidden,
			Menu.i("About Swiss").path("aboutSwiss") >> Hidden,
			Menu.i("About Knock-out").path("aboutKnockout") >> Hidden)

		LiftRules.setSiteMap(sitemap)

		LiftRules.localeCalculator = boxReq => {
			def newCookie(s: String) = {
				S.addCookie(HTTPCookie("lang", s).setPath("/").setMaxAge(10 * 365 * 24 * 60 * 60))
				new Locale(s)
			}
			def fromCookie = S.cookieValue("lang").map(new Locale(_))
			def defaultCalculator = LiftRules.defaultLocaleCalculator(boxReq)

			val locale = S.param("lang").map(newCookie).
					or(fromCookie).
					openOr(defaultCalculator)

			if (locale.getLanguage.toLowerCase.matches(".*(ua|be|kz|ge).*")) {
				new Locale("ru")
			} else {
				locale
			}
		}


		LiftRules.unloadHooks.append { () =>
			logger.info("roboCup actors shutdown")
			Core.system.shutdown()
		}

		Core // init the singleton

		//Init the jQuery module, see http://liftweb.net/jquery for more information.
		LiftRules.jsArtifacts = JQueryArtifacts
		JQueryModule.InitParam.JQuery = JQueryModule.JQuery172
		JQueryModule.init()

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
		//		S.addAround(DB.buildLoanWrapper)
	}
}
