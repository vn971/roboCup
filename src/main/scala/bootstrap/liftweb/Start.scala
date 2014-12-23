package bootstrap.liftweb

import java.io.File
import net.liftweb.common.Loggable
import net.liftweb.util.{LoggingAutoConfigurer, Props}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext


object Start extends App with Loggable {

	if (sys.props.get("user.dir") == Some("/usr/share/robocup") &&
			sys.props.get("user.name") == Some("robocup")
	) {
		// This is a dirty, dirty hack.
		// Normally, the run.mode should be configured with sbt-native-packager.
		// Unfortunately, I wasn't able to do so with reasonable time limits.
		System.setProperty("run.mode", "production")
	}

	LoggingAutoConfigurer().apply()

	logger.info("run.mode: " + Props.modeName)
	logger.info("system environment: " + sys.env)
	logger.info("system props: " + sys.props)
	logger.info("args: " + args.toList)

	startLift()

	/** Basic ways to start the jar are:
		* java -jar myjarname.jar
		* java -Drun.mode=production -jar myjarname.jar
		*/
	def startLift(): Unit = {
		logger.info("starting Lift server")

		val port: Int = Props.getInt("liftweb.port").getOrElse(8989)

		val webappDir = if (new File("src/main/webapp").exists()) {
			"src/main/webapp"
		} else {
			this.getClass.getClassLoader.getResource("webapp").toExternalForm
		}
		logger.info(s"using $webappDir as webappDir")

		val server = new Server(port)
		val context = new WebAppContext(webappDir, "/tournament")

		context.setWar(webappDir)

		server.setHandler(context)
		server.start()
		logger.info(s"Lift server started on port $port")
		server.join()
	}

}
