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
import ru.ya.vn91.robotour.Core
import ru.ya.vn91.robotour.Constants
import ru.ya.vn91.robotour.Constants._
import ru.ya.vn91.robotour.Utils

/** A class that's instantiated early and run.  It allows the application
 *  to modify lift's environment
 */
class Boot extends Loggable {
	def boot {

		// where to search snippet
		LiftRules.addToPackages("code")

		val adminPage = {
			val adminPageAddr = sys.props.get("admin.page")
			if (adminPageAddr.nonEmpty)
				Menu.i("Administration").path(adminPageAddr.get) >> Hidden
			else
				Menu.i("Administration").path("admin")
		}

		def sitemap = SiteMap(
			Menu.i("Main").path("index"),
			Menu.i("Registration").path("register"),
			Menu.i(tournamentName).path(if (isKnockout) "knockout" else "swiss"),
			adminPage,
			Menu.i("Chat").path("chat"),
			Menu.i("Language").path("language") >> Hidden,
			Menu.i("About Swiss").path("aboutSwiss") >> Hidden,
			Menu.i("About Knock-out").path("aboutKnockout") >> Hidden)

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
		//		S.addAround(DB.buildLoanWrapper)
	}
}
