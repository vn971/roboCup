package bootstrap.liftweb

import net.liftmodules.JQueryModule
import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.http.js.jquery.JQueryArtifacts
import net.liftweb.sitemap.Loc._
import net.liftweb.sitemap._
import ru.ya.vn91.robotour.Constants._
import ru.ya.vn91.robotour.Core


/** A class that's instantiated early and run.  It allows the application
 *  to modify lift's environment
 */
class Boot extends Loggable {
	def boot() {

		// where to search snippet
		LiftRules.addToPackages("code")

		val adminPage =
			sys.props.get("admin.page").map(
				Menu.i("Administration").path(_) >> Hidden
			).getOrElse(
				Menu.i("Administration").path("admin")
			)

		def sitemap = SiteMap(
			Menu.i("Main").path("index"),
			Menu.i("Registration").path("register"),
			Menu.i(tournamentName).path(if (isKnockout) "knockout" else "swiss"),
			adminPage,
			Menu.i("Chat").path("chat"),
			Menu.i("P. L. 2013").path("pl2013") >> Hidden,
			Menu.i("Language").path("language") >> Hidden,
			Menu.i("About Swiss").path("aboutSwiss") >> Hidden,
			Menu.i("About Knock-out").path("aboutKnockout") >> Hidden)

		LiftRules.setSiteMap(sitemap)

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
